package mensaje;

public class MensajeFicheroNoEncontrado extends Mensaje{
    
	private static final long serialVersionUID = 1L;
	private final String nombreFichero;
     
     public MensajeFicheroNoEncontrado(String origen, String destino, String nombreFichero) {
    	 super(TipoMensaje.MENSAJE_FICHERO_NO_ENCONTRADO, origen,destino);
    	 this.nombreFichero = nombreFichero;
     }

	public String getNombreFichero() {
		return nombreFichero;
	}
     
     
}
