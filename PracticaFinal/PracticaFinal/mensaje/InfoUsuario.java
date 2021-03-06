package mensaje;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

public class InfoUsuario implements Serializable {
   private static final long serialVersionUID = 1L;
   
   private final String id;
   private final InetAddress direccionIP;
   private final Set<String> fichCompartidos;
   
   public InfoUsuario(String id, InetAddress direccionIP, Set<String> fichCompartidos) {
		this.id = id;
		this.direccionIP = direccionIP;
		this.fichCompartidos = fichCompartidos;
	}

	public String getId() {
		return id;
	}
	
	public InetAddress getDireccionIP() {
		return direccionIP;
	}
	
	public Set<String> getFichCompartidos() {
		return fichCompartidos;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Id: "+id+"\nDireccionIP: "+direccionIP+ "\nFicheros Compartidos:\n");
		
		for(String s : fichCompartidos) {
			sb.append("  ");
			sb.append(s);
			sb.append("\n");
		}
		
		sb.append("\n");
		
		return sb.toString();
	}
	   
   
   
}
