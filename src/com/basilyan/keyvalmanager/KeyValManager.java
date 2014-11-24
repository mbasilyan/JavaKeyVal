package com.basilyan.keyvalmanager;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This is the key value manager.
 * Reads config file. Initializes machines and abstractions.
 * Needs to be aware of every available machine.
 * 
 * @author Michael Basilyan
 *
 */
public class KeyValManager {
	
	public static void main(String[] args) {
		String[] machines = {"127.0.0.1"}; //TODO: should read from file.
		int portNumber = 4442; //TODO: should read from file
		double pFalsePos = 0.0001;
		int numElems = 10000;
		BloomFilter<String> bf = new BloomFilter<String>(pFalsePos, numElems);
		
		boolean listening = true;

		try { 
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while (listening) {
				new KeyValManagerRequestHandler(serverSocket.accept(), machines, bf).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
		
	}
	

}
