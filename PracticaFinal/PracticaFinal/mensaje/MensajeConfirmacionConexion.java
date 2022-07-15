package mensaje;
public class MensajeConfirmacionConexion extends Mensaje {
	
	private static final long serialVersionUID = 1L;

	public MensajeConfirmacionConexion(String Origen, String Destino) {
	    	super(TipoMensaje.MENSAJE_CONFIRMACION_CONEXION, Origen, Destino);
	    }
}
