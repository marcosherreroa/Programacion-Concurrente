package servidor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

import mensaje.Mensaje;

public class Usuario { 
	private volatile InetAddress direccionIP;
	private volatile ObjectOutputStream fout;
	
	public Usuario(InetAddress direccionIP, ObjectOutputStream fout) {
		this.direccionIP = direccionIP;
		this.fout = fout;
	}
	
	public InetAddress getIP() {
		return direccionIP;
	}
	
	public void writeTo(Mensaje m) throws IOException {
		fout.writeObject(m);
	}
	
}
