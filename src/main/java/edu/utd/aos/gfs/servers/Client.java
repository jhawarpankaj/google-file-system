package edu.utd.aos.gfs.servers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.LocalHost;
import edu.utd.aos.gfs.utils.Sockets;

public class Client {
	public static void start() throws GFSException, IOException {
		Logger.info("Starting client server sockets for all external requests.");
		ServerSocket serverSocket = null;
		ClientImpl cimpl = new ClientImpl();
		try {
			serverSocket = new ServerSocket(LocalHost.getPort());
			while (true) {
				Socket receiverSocket = null;
				receiverSocket = serverSocket.accept();
				DataInputStream dis = new DataInputStream(receiverSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(receiverSocket.getOutputStream());
				Thread t = new ClientListener(receiverSocket, dis, dos, serverSocket, cimpl);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			serverSocket.close();
			throw new GFSException("Error while receiving message: " + e);
		}
	}

	public static void openCommandLineSocket() throws IOException {
		while (true) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String input = reader.readLine();
			String requestTS = Helper.getTimestamp().toString();
			input = input + GFSReferences.SEND_SEPARATOR + requestTS;
			Sockets.sendMessage(LocalHost.getName(), LocalHost.getPort(), input);
		}
	}
}
