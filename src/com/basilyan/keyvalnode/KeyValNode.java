package com.basilyan.keyvalnode;

import java.net.ServerSocket;
import java.io.IOException;
import java.util.HashMap;

/**
 * This represents the KV node. It launches.
 * Initializes a KV map. Listens for connections 
 * and adds/subtracts KV pairs. This is going to be simple & dumb by design.
 * 
 * 
 * @author Michael Basilyan
 *
 */
public class KeyValNode {

	public static void main(String[] args) {
		HashMap<String, String> hm = new HashMap<String, String>();

		if (args.length != 1) {
			System.err.println("Usage: java KeyValNode <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		boolean listening = true;

		try { 
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while (listening) {
				new KeyValNodeRequestHandler(serverSocket.accept(), hm).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}

}

