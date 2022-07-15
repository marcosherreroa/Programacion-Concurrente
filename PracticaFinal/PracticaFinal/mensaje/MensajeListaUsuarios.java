package mensaje;
public class MensajeListaUsuarios extends Mensaje {

	private static final long serialVersionUID = 1L;

	public MensajeListaUsuarios(String origen, String destino) {
		super(TipoMensaje.MENSAJE_LISTA_USUARIOS, origen, destino);
		
	}

}
