package mensaje;

public class MensajeCerrarConexion extends Mensaje {
    
	private static final long serialVersionUID = 1L;

	/* Este mensaje puede tener distintos significados segun el momento de la comunicacion,
	 pero esto tendr? que interpretarlo el servidor
	 Para desconectarse, el cliente manda un mensaje de este tipo, espera una confirmaci?n y despu?s
	 manda otro mensaje de este tipo                                                                 */

	public MensajeCerrarConexion(String origen, String destino) {
    	super(TipoMensaje.MENSAJE_CERRAR_CONEXION, origen, destino);
    }
}
