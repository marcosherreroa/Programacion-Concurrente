package mensaje;

import java.net.InetAddress;

public class MensajePreparadoServidorCliente extends Mensaje {
 
	private static final long serialVersionUID = 1L;
	private final String idEmisor;
	private final InetAddress dirIPEmisor;
     private final int puertoEmisor;
     private final String nombreFichero;
     
     public MensajePreparadoServidorCliente(String origen, String destino, String idEmisor,InetAddress dirIPEmisor, int puertoEmisor, String nombreFichero) {
    	 super(TipoMensaje.MENSAJE_PREPARADO_SERVIDOR_CLIENTE, origen, destino);
    	 this.idEmisor = idEmisor;
    	 this.dirIPEmisor = dirIPEmisor;
    	 this.puertoEmisor = puertoEmisor;
    	 this.nombreFichero = nombreFichero;
     }
     
    public String getIdEmisor() {
    	return idEmisor;
    }

	public InetAddress getDirIPEmisor() {
		return dirIPEmisor;
	}

	public int getPuertoEmisor() {
		return puertoEmisor;
	}
	
	public String getNombreFichero() {
		return nombreFichero;
	}
     
     
}
