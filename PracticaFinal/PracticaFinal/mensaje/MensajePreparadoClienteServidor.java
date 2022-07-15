package mensaje;

import java.net.InetAddress;

public class MensajePreparadoClienteServidor extends Mensaje {
    
	private static final long serialVersionUID = 1L;
	private final InetAddress dirIPEmisor;
	private final int puertoEmisor;
	private final String nombreFichero;
	private final String idReceptor;
	
	public MensajePreparadoClienteServidor(String origen, String destino, InetAddress dirIPEmisor, int puertoEmisor,
			String nombreFichero, String idReceptor) {
		super(TipoMensaje.MENSAJE_PREPARADO_CLIENTE_SERVIDOR, origen, destino);
		this.dirIPEmisor = dirIPEmisor;
		this.puertoEmisor = puertoEmisor;
		this.nombreFichero = nombreFichero;
		this.idReceptor = idReceptor;
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
	
	public String getIdReceptor() {
		return idReceptor;
	}
}
