package concurrencia;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ControladorReadersWriters {
    private volatile int nreaders;
    private volatile int nwriters;
    private volatile int delayreaders;
    private volatile int delaywriters;
    
    private final Lock l;
    private final Condition oktoread;
    private final Condition oktowrite;
    
    public ControladorReadersWriters() {
    	this.nreaders = 0;
    	this.nwriters = 0;
    	this.delayreaders = 0; 
    	this.delaywriters = 0;
    	
    	this.l = new ReentrantLock(true);
    	this.oktoread = l.newCondition();
    	this.oktowrite = l.newCondition();
    }
    
    public void request_read() throws InterruptedException {
    	l.lock();
    	
    	if(nwriters > 0 || delaywriters > 0) {
    		++delayreaders;
    		oktoread.await();
    		--delayreaders;
    	}
    	
    	nreaders++;
    	
    	l.unlock();
    }
    
    public void release_read() {
    	l.lock();
    	
    	--nreaders;
    	if(nreaders == 0 && delaywriters > 0) oktowrite.signal();
    	
    	l.unlock();
    	}
    
    public void request_write() throws InterruptedException {
    	l.lock();
    	
    	if(nreaders > 0 || nwriters > 0 || delayreaders > 0 || delaywriters > 0) {
    		++delaywriters;
    		oktowrite.await();
    		--delaywriters;
    	}
    	
    	++nwriters;
    	
    	l.unlock();
    }
    
    public void release_write() {
    	l.lock();
    	
    	--nwriters;
    	if(delayreaders > 0) oktoread.signalAll();
    	else if (delaywriters > 0)oktowrite.signal();
    	
    	l.unlock();
    }
}
