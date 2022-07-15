package mensaje;

public class MensajeRechazoConexion extends Mensaje {
	private static final long serialVersionUID = 1L;
	
	private final String causa;
    
    public MensajeRechazoConexion(String origen, String destino, String causa) {
		super(TipoMensaje.MENSAJE_RECHAZO_CONEXION, origen, destino);
		this.causa = causa;
	}
    
    public String getCausa() {
    	return causa;
    }
}
