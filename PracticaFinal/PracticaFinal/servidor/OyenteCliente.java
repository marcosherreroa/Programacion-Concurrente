package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import mensaje.*;

public class OyenteCliente extends Thread {
	enum EstadoOC{
		CONEXION,
		COMUNICACION,
		DESCONEXION,  //en este estado el oyente pendiente de resolver los ?ltimos env?os antes de cerrar la comunicacion
		DESACTIVADO
	}
	
	
	private String idCliente;
    private EstadoOC estado;
	
	private final Servidor servidor;
    private final Socket socket;
    
    
    public OyenteCliente(Servidor servidor, Socket socket) {
    	this.idCliente = "unknown";
    	this.estado = EstadoOC.CONEXION;
    	this.servidor = servidor;
    	this.socket = socket;
    }
    
  //como estamos respetando el protocolo, podemos asumir que no recibiremos ningun MENSAJE_TIPO_INESPERADO
    
    private void protocoloConexion(ObjectInputStream fin, ObjectOutputStream fout) throws IOException, InterruptedException, ClassNotFoundException {
    	TipoMensaje[] tiposEsperados = {TipoMensaje.MENSAJE_CONEXION, 
    			                        TipoMensaje.MENSAJE_CERRAR_CONEXION};
    	
    	
    	while(estado == EstadoOC.CONEXION) {
    		
    		Mensaje m = (Mensaje)fin.readObject();
    		
    		switch(m.getTipo()) {
    			case MENSAJE_CONEXION:
    				
    				//Guardar informacion del usuario
    				MensajeConexion mensaje = (MensajeConexion) m;
    				idCliente = mensaje.getOrigen();
    				Usuario user = new Usuario(socket.getInetAddress(),fout);
    				
    				if(servidor.addUsuario(idCliente, user, mensaje.getFichCompartidos())) {//si el usuario no estaba ya en el sistema
    					//Enviar confirmaci?n
    					fout.writeObject(new MensajeConfirmacionConexion("Servidor",idCliente)); 
    					
    					//Se pasa al protocolo de comunicaci?n 
    					estado = EstadoOC.COMUNICACION;
    				}
    				
    				else { //si el usuario estaba ya en el sistema
    					
    					//Se notifica al cliente
    					fout.writeObject(new MensajeRechazoConexion("Servidor",idCliente,"Usuario ya en el sistema"));
    					
    					//Se corta la comunicaci?n
    					estado = EstadoOC.DESACTIVADO;
    				}
    				
    				break;
    				
    			case MENSAJE_CERRAR_CONEXION:
    				
    				//Se corta la comunicacion
    		
    				estado = EstadoOC.DESACTIVADO;
    				
    				break;
    		
    			default:
    				//Se notifica al cliente los tipos de mensajes que se esperan en esta etapa de la comunicacion
    				fout.writeObject(new MensajeTipoInesperado("Servidor", idCliente, tiposEsperados));
    				
    				break;
    		    
    		}    		
    	}
    }
    
    private void protocoloComunicacion(ObjectInputStream fin, ObjectOutputStream fout) throws InterruptedException, ClassNotFoundException, IOException {
    	TipoMensaje[] tiposEsperados = {  TipoMensaje.MENSAJE_LISTA_USUARIOS,
    									  TipoMensaje.MENSAJE_PEDIR_FICHERO,
    									  TipoMensaje.MENSAJE_PREPARADO_CLIENTE_SERVIDOR,
    									  TipoMensaje.MENSAJE_CERRAR_CONEXION
    			                         };
    	
		while(estado == EstadoOC.COMUNICACION || estado == EstadoOC.DESCONEXION) {		
			Mensaje m = (Mensaje)fin.readObject();
			
			switch(m.getTipo()){
				case MENSAJE_LISTA_USUARIOS:
					//Obtener la lista de informaciones de los usuarios
					List<InfoUsuario> listaUsuarios = servidor.getInfoUsuarios();
					
					//Enviar lista
					fout.writeObject(new MensajeConfirmacionListaUsuarios("Servidor",idCliente, listaUsuarios));
					
					break;
					
				case MENSAJE_PEDIR_FICHERO:
					
					boolean completed = false;
					
					// hay que iterar en caso de que el emisor sea borrado de la tabla justo cuando le vamos a escribir 
					
					while(!completed) { 
						//Buscar un cliente, distinto del emisor del mensaje, que disponga de la informaci?n
						String nombreFichero = ((MensajePedirFichero) m).getNombreFichero();
						
						String idEmisor = servidor.buscarEmisor(nombreFichero,idCliente);
						
						if(idEmisor == null) {
							//Si nadie tiene el fichero solicitado, dec?rselo al cliente
							fout.writeObject(new MensajeFicheroNoEncontrado("Servidor",idCliente, nombreFichero));
							completed = true;
						}
						
						else {
							//Si encontramos un emisor, enviar por fout del emisor un MENSAJE_EMITIR_FICHERO
							completed = servidor.writeTo(idEmisor, 
									new MensajeEmitirFichero("Servidor",idEmisor,nombreFichero, idCliente));
						}
					
					}
					
					break;
				
				case MENSAJE_PREPARADO_CLIENTE_SERVIDOR:
					
					MensajePreparadoClienteServidor mensPrep = (MensajePreparadoClienteServidor) m;
					
					String idReceptor = mensPrep.getIdReceptor();
					servidor.writeTo(idReceptor,
							new MensajePreparadoServidorCliente("Servidor",idReceptor,mensPrep.getOrigen(), mensPrep.getDirIPEmisor(),
									mensPrep.getPuertoEmisor(), mensPrep.getNombreFichero()));
					/*hay garant?a de que el receptor estar? en la tabla, ya que no permitimos que un cliente que pida
					  un fichero se desconecte sin recibirlo */
					
					
					break;
					
					
				case MENSAJE_CERRAR_CONEXION:
				
					if(estado == EstadoOC.COMUNICACION) {
						//Eliminar informacion del usuario
						servidor.eliminarUsuario(idCliente);
						estado = EstadoOC.DESCONEXION;
						System.out.println("Cliente "+idCliente+" desconectado\n");
						
						/*a partir de aqui ya no es posible que otro oyente cliente se comunique con el
						 cliente asociado a este flujo, pero quizas este oyente cliente aun reciba algun
						 MENSAJE_PREPARADO_CLIENTE_SERVIDOR por parte de el. El cliente tendra que completar
						 todas las transferencias que le hab?an pedido antes antes de poder desconectarse */
						
						//Enviar confirmaci?n
						fout.writeObject(new MensajeConfirmacionCerrarConexion("Servidor",idCliente));
					}
					
					else {
						estado = EstadoOC.DESACTIVADO;
					}
				
					break;
					
				default:
					//Se notifica al cliente los tipos de mensajes que se esperan en esta etapa de la comunicacion
    				fout.writeObject(new MensajeTipoInesperado("Servidor", idCliente, tiposEsperados));

    				break;
			}
   
    	}
    }
    
    public void run() {
    	try {

    	ObjectInputStream fin = new ObjectInputStream(socket.getInputStream());
    	ObjectOutputStream fout = new ObjectOutputStream(socket.getOutputStream());
    	
    	protocoloConexion(fin,fout);
    	protocoloComunicacion(fin,fout);
    	
    	//Cerrar el canal
    	socket.close();
    	
    	} catch (IOException | ClassNotFoundException e) {
    		System.out.println("\nOyenteCliente "+idCliente+": Error al leer mensaje del cliente\n");
    		e.printStackTrace();
    		
		} catch (InterruptedException e) {
			System.out.println("\nOyenteCliente "+idCliente+": Thread interumpido cuando esperaba en un lock\n");
			e.printStackTrace();
			
		} 
    }
}
