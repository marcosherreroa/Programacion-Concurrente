package mensaje;

import java.util.Set;

public class MensajeConexion extends Mensaje {

	private static final long serialVersionUID = 1L;
	private Set<String> fichCompartidos;
	
    public MensajeConexion(String Origen, String Destino, Set<String> fichCompartidos) {
    	super(TipoMensaje.MENSAJE_CONEXION, Origen, Destino);
    	this.fichCompartidos = fichCompartidos;
    }
    
    public Set<String> getFichCompartidos() {
    	return fichCompartidos;
    }
    
}
