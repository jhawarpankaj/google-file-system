package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import com.google.gson.JsonObject;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;

public class MetaListener extends Thread {
	final Socket worker;
	final DataInputStream dis;
    final DataOutputStream dos;
    public static final ReentrantLock lock = new ReentrantLock();
    
    public MetaListener(Socket worker, DataInputStream dis, DataOutputStream dos) {
    	this.worker = worker;
    	this.dis = dis;
    	this.dos = dos;
    }
    
    @Override
    public void run(){
		try {
			// message received on socket.
            String received = dis.readUTF(); 
            // server from which the message has come.
            String server = this.worker.getInetAddress().getHostName();
            
            Logger.debug("Received message: " + received);            
            
            String command = Helper.getCommand(received);
            String message = Helper.getMessage(received);
            
            switch(command) {
            
            	case GFSReferences.HEARTBEAT:            		
            		JsonObject heartbeatJson = Helper.getParsedHeartBeat(message);
            		Logger.debug("Parsed heart beat message: " + heartbeatJson);            		
            		// @Amtul: Take appropriate action on the parsed heartbeat message.
            		// takeAction(server, heartbeatJson); 
            		// Below is a sample method to iterate on the gson Json. [to be deleted]
            		Helper.iterateHeartBeat(server, heartbeatJson);            		
            		break;
            	
            	// @Amtul: your other implementation goes below.
            	case GFSReferences.CREATE:
            	case GFSReferences.READ:
            	case GFSReferences.APPEND:
            		break;
            		
        		default:
        			throw new GFSException("Unidentified input: " + command 
        					+ " received on META server!!");            			
            }
    		
    		
    		// All our code for different input messages goes here.
    		// switch(parseinput(received())){
    		// case "INPUT1":
    		// case "INPUT2":

    	}catch(Exception e) {
    		Logger.error("Error while performing client request: " + e);
    	}
    }
}
