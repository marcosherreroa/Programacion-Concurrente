package mensaje;

public class MensajeTipoInesperado extends Mensaje {
	private static final long serialVersionUID = 1L;
	
	private final TipoMensaje[] tiposEsperados;
	
    public MensajeTipoInesperado(String origen, String destino, TipoMensaje[] tiposEsperados) {
    	super(TipoMensaje.MENSAJE_TIPO_INESPERADO,origen,destino);
    	this.tiposEsperados = tiposEsperados;
    }
    
    public TipoMensaje[] getTiposEsperados() {
    	return tiposEsperados;
    }
}
