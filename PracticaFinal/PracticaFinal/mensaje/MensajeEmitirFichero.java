package mensaje;

public class MensajeEmitirFichero extends Mensaje {
	private static final long serialVersionUID = 1L;
	
	private final String nombreFichero;
    private final String idReceptor;
     
     public MensajeEmitirFichero(String origen, String destino, String nombreFichero, String idReceptor ) {
    	super(TipoMensaje.MENSAJE_EMITIR_FICHERO,origen, destino);
    	this.nombreFichero = nombreFichero;
    	this.idReceptor = idReceptor;
     }

	public String getNombreFichero() {
		return nombreFichero;
	}

	public String getIdReceptor() {
		return idReceptor;
	}
     
}
