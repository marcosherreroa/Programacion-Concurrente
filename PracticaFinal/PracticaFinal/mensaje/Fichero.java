package mensaje;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Fichero implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String nombre;
	private final byte[] contenido;
    
    public Fichero(String nombre, String pathFicherosCompartidos) throws IOException {
		this.nombre = nombre;
		this.contenido = Files.readAllBytes(Paths.get(pathFicherosCompartidos+"/"+nombre));
	}


	public String getNombre() {
		return nombre;
	}
	
	public byte[] getContenido() {
		return contenido;
	}
	
	public void writeFile(String pathFicherosDescargados) throws FileNotFoundException, IOException {
		try(FileOutputStream stream = new FileOutputStream(pathFicherosDescargados+"/"+nombre)){
			stream.write(contenido);
		}
	}

}
