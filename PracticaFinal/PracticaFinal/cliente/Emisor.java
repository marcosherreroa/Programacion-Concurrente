package cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import mensaje.Fichero;

public class Emisor extends Thread {
	private final String idCliente;
    private final ServerSocket serverSocket;
    private Socket socket;
    private final int puerto;
    private final Fichero fichero;
    private final String idReceptor;
    
    public Emisor(String idCliente, String nombreFichero, String pathFicherosCompartidos, String idReceptor) throws IOException {
    	this.idCliente = idCliente;
    	this.serverSocket = new ServerSocket(0);
    	this.puerto = serverSocket.getLocalPort();
    	this.fichero = new Fichero(nombreFichero, pathFicherosCompartidos);
    	this.idReceptor = idReceptor;
    }
    
    public int getPuerto() {
    	return puerto;
    }
    
    public void run() {
    	try {
			socket = serverSocket.accept();
			ObjectOutputStream fout = new ObjectOutputStream(socket.getOutputStream());
			fout.writeObject(fichero);
			
			socket.close();
			
		} catch (IOException e) {
			System.out.println("Emisor de "+fichero.getNombre()+" desde "+idCliente+" a "+idReceptor+" : Error de IO");
			e.printStackTrace();
		}
    }
}
