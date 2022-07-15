package mensaje;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
 
   private static final long serialVersionUID = 1L;
   
   private final TipoMensaje tipo;
   private final String origen;
   private  final String destino;
   
   public Mensaje(TipoMensaje tipo, String origen, String destino) {
	   this.tipo = tipo;
	   this.origen = origen;
	   this.destino = destino;
   }
   
	public TipoMensaje getTipo() {
		return tipo;
	}
	
	public String getOrigen() {
		return origen;
	}
	
	public String getDestino() {
		return destino;
	}

   
}
