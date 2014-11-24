package com.basilyan.keyvalnode;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Implements the protocol and the get/set commands.
 * @author Michael Basilyan
 *
 */

public class KeyValNodeRequestHandler extends Thread {

	final static String CRLF = "\r\n";
	private Socket socket = null;
	private HashMap<String, String> hm = null;

	/**
	 * 
	 * @param socket - the connection socket
	 * @param hm - key value map. 
	 */
	public KeyValNodeRequestHandler(Socket socket, HashMap<String, String> hm)
	{
		super("KeyValNodeRequestHandlerThread_" + socket.getInetAddress().toString());
		this.socket = socket;
		this.hm = hm;
		System.out.println("Connected to: " + socket.getInetAddress() + " on " + socket.getLocalPort());
	}

	@Override
	public void run() {
		/*
		 * Inspired by: 
		 * https://github.com/grahamking/Key-Value-Polyglot/blob/master/Memg.java
		 * 
		 */
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				String inputLine = in.readLine();
				if(inputLine.equals("")) break;
				String[] inputLineSplit = inputLine.split(" ");
				String cmd = inputLineSplit[0];
				if(cmd.toLowerCase().equals("get")) {
					synchronized(hm) { 
						System.out.println("Recieved get command:" + inputLine);
						String key = inputLineSplit[1];
						String val;
						if(hm.containsKey(key)) {
							val = hm.get(key);
							out.printf("VALUE %s 0 %d\r\n", key, val.length());
							out.printf("%s\r\n",val);
						}
						out.printf("END\r\n");
					}
				} else if(cmd.toLowerCase().equals("set")) {
					synchronized(hm)
					{
						System.out.println("Recieved set command:" + inputLine);
						String key = inputLineSplit[1];
						int length = Integer.parseInt(inputLineSplit[4]);
						char[] buf = new char[length + 2]; //This is not 100% correct for handling unicode bytes
						int index = 0;
						while (index < buf.length) {
							int len = in.read(buf, index, buf.length - index);
							if (len == -1)
								break;
							index += len;
						}
						String val = new String(buf, 0, length);
						hm.put(key, val);
						out.printf("STORED\r\n");
					}
				} else {
					System.out.println("Unknown cmd: " + cmd);
					break;
				}
				out.flush();
			}
		}
		catch(Exception ex) {
			System.out.println(ex);
		}
	}

}
