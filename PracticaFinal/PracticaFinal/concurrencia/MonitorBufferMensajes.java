package concurrencia;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorBufferMensajes {
     private final String [] bufferMensajes;
     private final int  size;
     private volatile int nmensajes;
     private volatile int indProd;
     private volatile int indCons;
     
     private Lock l;
     private Condition not_full;
     
     public MonitorBufferMensajes(int size) {
    	 this.bufferMensajes = new String[size];
    	 this.size = size;
    	 this.nmensajes = 0;
    	 this.indProd = 0;
    	 this.indCons = 0;
    	 
    	 this.l = new ReentrantLock(true);
    	 this.not_full = l.newCondition();
     }
     
     public void depositarMensaje(String mensaje) throws InterruptedException {
    	 l.lock();
    	 
    	 while(nmensajes == size)not_full.await();
    	 
    	 bufferMensajes[indProd] = mensaje;
    	 indProd = (indProd+1)%size;
    	 ++nmensajes;
    	 
    	 l.unlock();
     }
     
     
     public void mostrarMensajes() {
    	 l.lock();
    	 
    	 while(nmensajes > 0) {
    		 System.out.println(bufferMensajes[indCons]);
    		 indCons = (indCons + 1)%size;
    		 --nmensajes;
    	}
    	 
    	 
        not_full.signalAll();	 
        l.unlock();
     }
}
