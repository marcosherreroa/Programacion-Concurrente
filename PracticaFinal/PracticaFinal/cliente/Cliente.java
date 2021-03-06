package cliente;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Semaphore;

import concurrencia.*;
import mensaje.*;

//Nombre : Marcos Herrero Agust?n   Grado: DG
//Pr?ctica individual

public class Cliente {
	private final String id;
	private final Scanner stdinput;
	private volatile boolean active;
	private volatile int recepcionesEnCurso;
	
	private final String pathFicherosCompartidos;
	private final String pathFicherosDescargados;
	
	private final InetAddress dirIP;
	private final InetAddress dirIPServidor;
	private final Socket socketServidor;
	private final ObjectOutputStream fout;
	
	private final MonitorBufferMensajes bufferMensajes; // buffer en el que los procesos OS y Receptor depositan mensajes 
	                                                    // para el cliente que este no espera de manera s?ncrona
	                                                    //(por ejemplo, la notificaci?n de que se ha descargado un fichero)

	private final Semaphore mutRecepciones; //para incrementar/decrementar recepciones en curso en exclusi?n mutua
	private final Semaphore esperaOyente; // para sincronizarse con el oyente servidor
	
	public Cliente(String id,Scanner stdinput) throws IOException {
		this.id = id;
		this.stdinput = stdinput;
		this.active = true;
		this.recepcionesEnCurso = 0;
		this.pathFicherosCompartidos = "ficherosCompartidos/"+id;
		this.pathFicherosDescargados = "ficherosDescargados/"+id;
				
		this.dirIP = InetAddress.getLocalHost();
		this.dirIPServidor = InetAddress.getByName("192.168.1.90");//192.168.1.90
		Files.createDirectories(Paths.get(pathFicherosDescargados));
		
		this.socketServidor = new Socket(dirIPServidor, 500);
		this.fout = new ObjectOutputStream(socketServidor.getOutputStream());
		
		this.bufferMensajes = new MonitorBufferMensajes(10);
		
		this.mutRecepciones = new Semaphore(1,true);
		this.esperaOyente = new Semaphore(0,true); 
		
	}
	
	public String getId() {
		return id;
	}
	
	public InetAddress getDirIP() {
		return dirIP;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getPathFicherosCompartidos() {
		return pathFicherosCompartidos;
	}
	
	public String getPathFicherosDescargados() {
		return pathFicherosDescargados;
	}
	
	
	public void decrementRecepcionesEnCurso() throws InterruptedException {
		mutRecepciones.acquire();
		--recepcionesEnCurso;
		mutRecepciones.release();
	}
	
	public void depositarMensaje(String mensaje) throws InterruptedException {
		bufferMensajes.depositarMensaje(mensaje);
	}
	
	public Semaphore getMutRecepciones() {
		return mutRecepciones;
	}
	
	public void execute() throws IOException, InterruptedException {
		//Obtiene los ficheros compartidos
		
		Set<String> fichCompartidos = new HashSet<String>();
		File sharedDirectory = new File(pathFicherosCompartidos);
		sharedDirectory.mkdir();
		File [] arrayF = sharedDirectory.listFiles();
		
		for(File f : arrayF) {
	        fichCompartidos.add(f.getName());
		}
		
		ObjectInputStream fin = new ObjectInputStream(socketServidor.getInputStream());
		
		OyenteServidor os = new OyenteServidor(this, fin,fout, esperaOyente);
		os.start();
		
		//Envio mensaje conexi?n
		fout.writeObject(new MensajeConexion(id,"Servidor",fichCompartidos));
		
		//Esperar a recibir la confirmaci?n de conexi?n
		esperaOyente.acquire();
		
		while(active) {
			
			//En cada iteraci?n del cliente,  mostrar el men?, mostramos los mensajes que hayan quedado en el buffer
	
			bufferMensajes.mostrarMensajes();
			
			//Mostramos el men?
			
			System.out.println();
			System.out.println("=====================");
			System.out.println("MENU");
			System.out.println("=====================");
			System.out.println();
			
			System.out.println("?Qu? desea hacer?");
			System.out.println("0: Consultar lista de usuarios");
			System.out.println("1: Pedir fichero");
			System.out.println("2: Salir");
			System.out.println("Introduzca opcion [0-2] : ");
			
			//El usuario selecciona una opci?n
			
			int option = stdinput.nextInt();
			
			switch(option) {
			case 0:
				//Enviar mensaje lista usuarios
				fout.writeObject(new MensajeListaUsuarios(id,"Servidor"));
				
				//Esperar a recibir la lista de usuarios
				esperaOyente.acquire();
				
				break;
				
			case 1:
				//Pedir al usuario el nombre del fichero
				System.out.println("Introduzca el nombre del fichero buscado:");
			    String nombreFichero = stdinput.next();
			    
			    //Enviar mensaje pedir fichero
				fout.writeObject(new MensajePedirFichero(id,"Servidor",nombreFichero));
				
				mutRecepciones.acquire();
				++recepcionesEnCurso;
				mutRecepciones.release();
				
				
				System.out.println("\nGestionando la descarga del fichero "+nombreFichero+"\n");
				
				break;
				
			case 2:
				
				if(recepcionesEnCurso > 0) {
					System.out.println("\nNo puedes salir hasta que recibas las "+recepcionesEnCurso+" recepciones pendientes\n");
				}
				
				else {
					//Antes de salir mostramos los mensajes pendientes
					bufferMensajes.mostrarMensajes();
					
					//Envio mensaje cierre de conexion
					fout.writeObject(new MensajeCerrarConexion(id,"Servidor"));
		
					//Esperar a recibir la confirmacion de cierre de conexion
					esperaOyente.acquire();
				}
					
				break;
			}
		}
		
		//Cerrar canal
		socketServidor.close();
	}
	
    public static void main(String[] args) {
    	System.out.print("Introduzca su nombre de usuario: ");
    	Scanner stdinput = new Scanner(System.in);
    	String id = stdinput.nextLine();
    
		try {
			Cliente cliente = new Cliente(id,stdinput);
			cliente.execute();
			
		} catch (IOException e) {
			System.out.println("Cliente "+id+" : Error de IO");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Cliente "+id+" : Thread interrumpido cuando esperaba en un sem?foro");
			e.printStackTrace();
		}
    	
    	stdinput.close();
    }
}
