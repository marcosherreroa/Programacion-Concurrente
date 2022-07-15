package mensaje;
public class MensajePedirFichero extends Mensaje {
	
	private static final long serialVersionUID = 1L;
	private final String nombreFichero;
    
	public MensajePedirFichero(String origen, String destino, String nombreFichero) {
		super(TipoMensaje.MENSAJE_PEDIR_FICHERO, origen, destino);
		this.nombreFichero = nombreFichero;
	}

	public String getNombreFichero() {
		return nombreFichero;
	}

}
