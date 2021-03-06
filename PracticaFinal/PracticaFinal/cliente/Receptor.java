package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

import mensaje.Fichero;

public class Receptor extends Thread {
    private final Cliente cliente;
    private final Socket socket;
    private final String nombreFichero;
    private final String pathFicherosDescargados;
    private final String idEmisor;
    
    
    
    public Receptor(Cliente cliente, InetAddress dirIPEmisor, int puertoEmisor, String nombreFichero, 
    		String pathFicherosDescargados, String idEmisor) throws IOException {
    	this.cliente = cliente;
    	this.socket = new Socket(dirIPEmisor, puertoEmisor);
    	this.nombreFichero = nombreFichero;
    	this.pathFicherosDescargados = pathFicherosDescargados;
    	this.idEmisor = idEmisor;
    }
    
    
    public void run() {
    	try {
    		
			ObjectInputStream fin = new ObjectInputStream(socket.getInputStream());
			Fichero fichero = (Fichero) fin.readObject();
			fichero.writeFile(pathFicherosDescargados);
			cliente.depositarMensaje("Fichero "+nombreFichero+" recibido con ?xito");
			
			cliente.decrementRecepcionesEnCurso();
			socket.close();
			
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Receptor de "+nombreFichero+" desde "+idEmisor+" a "+cliente.getId()+" : Error al leer mensaje del emisor");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Receptor de "+nombreFichero+" desde "+idEmisor+" a "+cliente.getId()+" : Thread interrumpido mientras esperaba en un lock");
			e.printStackTrace();
		}
    }
}
