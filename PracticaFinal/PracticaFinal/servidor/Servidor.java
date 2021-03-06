package servidor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import concurrencia.ControladorReadersWriters;
import mensaje.InfoUsuario;
import mensaje.Mensaje;

//Nombre : Marcos Herrero Agust?n   Grado: DG
//Pr?ctica individual

public class Servidor {
   //Informaci?n servidor
   private final InetAddress direccionIP;
   private final int puerto;
   private final ServerSocket serverSocket;	
	
  //Informacion clientes
  private volatile Map<String,Usuario> tablaUsuarios; 
  private volatile Map<String,Set<String>> tablaInformacion;
  
  //Concurrencia
  private final ControladorReadersWriters accesoTablaUsuarios;
  private final ControladorReadersWriters accesoTablaInformacion;
  
  
  private Servidor() throws IOException {
	  this.direccionIP = InetAddress.getLocalHost();
	  this.serverSocket = new ServerSocket(500);
	  this.puerto = 500;
	  
	  this.tablaUsuarios = new HashMap<String,Usuario>();
	  this.tablaInformacion = new HashMap<String, Set<String>>();
	  
	  this.accesoTablaUsuarios = new ControladorReadersWriters();
	  this.accesoTablaInformacion = new ControladorReadersWriters();
  }
  
  public boolean addUsuario(String idUser, Usuario u, Set<String> fichCompartidos) throws InterruptedException {
	  accesoTablaUsuarios.request_write();
	  
	  if(tablaUsuarios.containsKey(idUser)) {
		  System.out.println("Servidor: Cliente "+idUser+" ya estaba registrado\n");
		  accesoTablaUsuarios.release_write();
		  return false;
	  }
	  
	  tablaUsuarios.put(idUser, u);
	  accesoTablaUsuarios.release_write();
	  
	  accesoTablaInformacion.request_write();
	  tablaInformacion.put(idUser, fichCompartidos);
	  accesoTablaInformacion.release_write();
	  
	  System.out.println("Servidor: Cliente "+idUser+" a?adido\n");
	  
	  return true;
  }
  
  public List<InfoUsuario> getInfoUsuarios() throws InterruptedException{
	  List<InfoUsuario> listaUsuarios = new ArrayList<InfoUsuario>();
	  
	  accesoTablaUsuarios.request_read();
	  accesoTablaInformacion.request_read();
	  
	  for(Entry<String,Usuario> e : tablaUsuarios.entrySet()) {
		  listaUsuarios.add(new InfoUsuario(e.getKey(), e.getValue().getIP(), tablaInformacion.get(e.getKey())));
	  }
	  
	  accesoTablaInformacion.release_read();
	  accesoTablaUsuarios.release_read();
	  
	  return listaUsuarios;
  }
  
  public String buscarEmisor(String nombreFichero, String idReceptor) throws InterruptedException {
	  accesoTablaInformacion.request_read();
	  
	  String idEmisor = null;
	  for(Entry<String,Set<String>> e : tablaInformacion.entrySet()) {
		  if(e.getKey() != idReceptor) {
			  if(e.getValue().contains(nombreFichero)) {
				  idEmisor = e.getKey();
				  break;
			  }
		  }  
	  }
	  
	  accesoTablaInformacion.release_read();
	  
	  return  idEmisor;
  }
  
  public boolean writeTo(String idUsuario, Mensaje m) throws InterruptedException, IOException {
	  //devuelve false si no encuentra el usuario especificado en la tabla de usuarios
	  accesoTablaUsuarios.request_read();
	  
	  Usuario u = tablaUsuarios.get(idUsuario);
	  if(u != null) u.writeTo(m);
	  
	  accesoTablaUsuarios.release_read();
	  
	  return (u != null);
  }
  
  public void eliminarUsuario(String id) throws InterruptedException {
	  accesoTablaUsuarios.request_write();
	  tablaUsuarios.remove(id);
	  accesoTablaUsuarios.release_write();
	  
	  accesoTablaInformacion.request_write();
	  tablaInformacion.remove(id);
	  accesoTablaInformacion.release_write();
  }
  
  public void execute() throws IOException {
	  System.out.println("Servidor escuchando en puerto "+puerto+"\n");
	  
	  while (true) {
		  Socket socket = serverSocket.accept();
		  OyenteCliente oc = new OyenteCliente(this,socket);
		  oc.start();
	  }
  }
  
  
  public static void main(String[] args){
	Servidor servidor;
	try {
		servidor = new Servidor();
		servidor.execute();
		
	} catch (IOException e) {
		System.out.println("Servidor: error de IO");
		e.printStackTrace();
	}
	  
  }
}
