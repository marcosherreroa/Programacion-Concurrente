package mensaje;

import java.util.List;

public class MensajeConfirmacionListaUsuarios extends Mensaje {
	private static final long serialVersionUID = 1L;
	
	private List<InfoUsuario> listaInfo;

	public MensajeConfirmacionListaUsuarios(String origen, String destino, List<InfoUsuario> listaInfo) {
		super(TipoMensaje.MENSAJE_CONFIRMACION_LISTA_USUARIOS, origen, destino);
		this.listaInfo = listaInfo;
	}

	public List<InfoUsuario> getListaInfo(){
		return listaInfo;
	}
}
