package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import mensaje.*;

public class OyenteServidor extends Thread {
	enum EstadoOS{
		CONEXION,
		COMUNICACION,
		DESACTIVADO
	}
	
	private EstadoOS estado;
	
    private Cliente cliente;
    private ObjectInputStream fin;
    private ObjectOutputStream fout;
    private Semaphore esperaOyente;
    
    private List<Emisor> listaEmisores;
	
    public OyenteServidor(Cliente cliente, ObjectInputStream fin, ObjectOutputStream fout, Semaphore esperaOyente) {
    	this.estado = EstadoOS.CONEXION;
    	
    	this.cliente = cliente;
    	this.fin = fin;
    	this.fout = fout;
    	this.esperaOyente = esperaOyente;
    	
    	this.listaEmisores = new ArrayList<Emisor>();
    }
    
  //como estamos respetando el protocolo, podemos asumir que no recibiremos ning?n MENSAJE_TIPO_INESPERADO
    
    public void protocoloConexion() throws ClassNotFoundException, IOException {
    	TipoMensaje [] tiposEsperados = { TipoMensaje.MENSAJE_CONFIRMACION_CONEXION,
    			                          TipoMensaje.MENSAJE_RECHAZO_CONEXION
    			                          };
    	
    	
    	while(estado == EstadoOS.CONEXION) {
    		
    		Mensaje m = (Mensaje) fin.readObject();
    		
    		switch(m.getTipo()) {
    			case MENSAJE_CONFIRMACION_CONEXION:
    				
    				//Se comunica que la conexi?n ha sido establecida
    				System.out.println("\nOyenteServidor "+cliente.getId()+" : Conexi?n establecida\n");
    				
    				//Se pasa al protocolo de comunicaci?n
    				estado = EstadoOS.COMUNICACION;
    				
    				//Se permite ejecutar al cliente
					esperaOyente.release();
					
					break;
					
    			case MENSAJE_RECHAZO_CONEXION:
    				MensajeRechazoConexion mensajeRech = (MensajeRechazoConexion) m;
    				
    				//Se comunica la causa del rechazo por la salida estandar
    				System.out.println("\nOyenteServidor "+cliente.getId()+" : Conexi?n rechazada");
    				System.out.println("Causa :"+mensajeRech.getCausa());
    				
    				//Se corta la comunicaci?n
    				estado = EstadoOS.DESACTIVADO;
    				
    				//Se notifica al cliente que ha desconectarse
    				cliente.setActive(false);
    				
    				//Se permite ejecutar al cliente
    				esperaOyente.release();
    				
    				break;
    				
    				
    			default:
    				//Se notifica al servidor los tipos de mensajes que se esperan en esta etapa de la comunicacion
    				fout.writeObject(new MensajeTipoInesperado(cliente.getId(), "Servidor", tiposEsperados));
    				
    				break;
    		    
    		}
    	}
    }
    
    public void protocoloComunicacion() throws ClassNotFoundException, IOException, InterruptedException {
    	TipoMensaje[] tiposEsperados = { TipoMensaje.MENSAJE_CONFIRMACION_LISTA_USUARIOS,
    			                         TipoMensaje.MENSAJE_EMITIR_FICHERO,
    			                         TipoMensaje.MENSAJE_PREPARADO_SERVIDOR_CLIENTE,
    			                         TipoMensaje.MENSAJE_FICHERO_NO_ENCONTRADO,
    			                         TipoMensaje.MENSAJE_CONFIRMACION_CERRAR_CONEXION
    			                         };
    	
    	
    	while (estado == EstadoOS.COMUNICACION) {
			
			Mensaje m = (Mensaje) fin.readObject();
			
			switch(m.getTipo()) {
			
				case MENSAJE_CONFIRMACION_LISTA_USUARIOS:
					List<InfoUsuario> l = ((MensajeConfirmacionListaUsuarios) m).getListaInfo();
					
					System.out.println("\nUsuarios en el sistema:");
					for(InfoUsuario info : l) {
						System.out.println(info);
					}
					
					//Se permite ejecutar al cliente
					esperaOyente.release();
					
					break;
			
					
				case MENSAJE_EMITIR_FICHERO:
					
					MensajeEmitirFichero mensEmitir = (MensajeEmitirFichero) m;
					
					//Crear proceso Emisor
					
					Emisor emisor = new Emisor(cliente.getId(), mensEmitir.getNombreFichero(), 
							cliente.getPathFicherosCompartidos(), mensEmitir.getIdReceptor());
					emisor.start();
					listaEmisores.add(emisor);
					
					//Enviar MENSAJE_PREPARADO_CLIENTE_SERVIDOR a mi OC
			
					fout.writeObject(new MensajePreparadoClienteServidor(cliente.getId(),"Servidor", cliente.getDirIP(), 
							emisor.getPuerto(), mensEmitir.getNombreFichero(), mensEmitir.getIdReceptor()));
					
					break;
					
				case MENSAJE_PREPARADO_SERVIDOR_CLIENTE:
					
					MensajePreparadoServidorCliente mensPrep = (MensajePreparadoServidorCliente) m;
					
					Receptor receptor = new Receptor(cliente,mensPrep.getDirIPEmisor(),mensPrep.getPuertoEmisor(),
							mensPrep.getNombreFichero(), cliente.getPathFicherosDescargados(), mensPrep.getIdEmisor());
					receptor.start();
					
					break;
					
				case MENSAJE_FICHERO_NO_ENCONTRADO:
					
					String nombreFichero = ((MensajeFicheroNoEncontrado)m).getNombreFichero();
					cliente.depositarMensaje("Ning?n cliente est? compartiendo el fichero "+nombreFichero+" en este momento. Int?ntalo mas tarde");
					cliente.decrementRecepcionesEnCurso();
					
					break;
					
				case MENSAJE_CONFIRMACION_CERRAR_CONEXION:
					
					System.out.println("\nOyenteServidor "+cliente.getId()+" : Adi?s\n");
					
					//Manda un nuevo mensaje que completa el protocolo de desconexion
					//(informa al servidor de que ya no vamos a recibir mas mensajes por aqui)
				
					fout.writeObject(new MensajeCerrarConexion(cliente.getId(),"Servidor"));
					
					//Se corta la comunicacion
					estado = EstadoOS.DESACTIVADO;
					
					// Se notifica al cliente que ha de desconectarse
					cliente.setActive(false);
					
					//Se permite ejecutar al cliente
					esperaOyente.release();
					
					break;
					
				default :
					//Se notifica al servidor los tipos de mensajes que se esperan en esta etapa de la comunicacion
					
					fout.writeObject(new MensajeTipoInesperado(cliente.getId(), "Servidor", tiposEsperados));
					
					break;
			}	
    	}
    }
    
	public void run() {
		try {
			protocoloConexion();
			protocoloComunicacion();
		
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("\nOyenteServidor "+cliente.getId()+": Error al leer mensaje del servidor\n");
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			System.out.println("\nOyenteServidor "+cliente.getId()+": Thread interrumpido mientras esperaba en un lock\n");
			e.printStackTrace();
		} 
	}
}
