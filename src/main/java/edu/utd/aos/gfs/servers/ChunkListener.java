package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

public class ChunkListener extends Thread {
	final Socket worker;
	final DataInputStream dis;
    final DataOutputStream dos;
    public static final ReentrantLock lock = new ReentrantLock();
    
    public ChunkListener(Socket worker, DataInputStream dis, DataOutputStream dos) {
    	this.worker = worker;
    	this.dis = dis;
    	this.dos = dos;
    }
    
    @Override
    public void run(){
    	try {
    		String received = dis.readUTF();
    		// All our code for different input messages goes here.
    		// switch(parseinput(received())){
    		// case "INPUT1":
    		// case "INPUT2":
    	}catch(Exception e) {
    		Logger.error("Error while performing client request: " + e);
    	}
    }
}
