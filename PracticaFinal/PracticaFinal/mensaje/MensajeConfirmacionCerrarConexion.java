package mensaje;

public class MensajeConfirmacionCerrarConexion extends Mensaje {
	private static final long serialVersionUID = 1L;

	public MensajeConfirmacionCerrarConexion(String Origen, String Destino) {
	    	super(TipoMensaje.MENSAJE_CONFIRMACION_CERRAR_CONEXION, Origen, Destino);
	    }
}
