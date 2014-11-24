package com.basilyan.keyvalmanager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Class that handles the actual request as a thread. 
 * Can add / set and get keys (vals).
 * @author Michael Basilyan
 *
 */

public class KeyValManagerRequestHandler extends Thread {

	private Socket socket;
	private String[] machines;
	private KeyValAbstractNode[] nodes;
	BloomFilter<String> bf;


	public KeyValManagerRequestHandler(Socket socket, String[] machines, BloomFilter<String> bf) throws UnknownHostException {
		this.socket = socket;
		this.machines = machines;
		this.bf = bf;
		for (int i = 0; i < machines.length; i++) {
			nodes[i] = new KeyValAbstractNode(machines[i]);
		}
	}

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true) {
				String inputLine = in.readLine();
				if(inputLine.equals("")) break;
				String[] inputLineSplit = inputLine.split(" ");
				String cmd = inputLineSplit[0];

				if(cmd.toLowerCase().equals("get")) {
					System.out.println("Recieved get command:" + inputLine);
					String key = inputLineSplit[1];
					String val = get(key);
					out.printf("VALUE %s 0 %d\r\n", key, val.length());
					out.printf("%s\r\n",val);
				}
				out.printf("END\r\n");

				if(cmd.toLowerCase().equals("set")) {
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
					if(set(key, val))
						out.printf("STORED\r\n");

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

	/**
	 * Sets a key-val pair to the distributed store.
	 * @param key Key
	 * @param val Value
	 * @return true for success; false for failure
	 */
	public boolean set(String key, String val)
	{
		int machineNum = mapKeyToMachine(key);
		if(!nodes[machineNum].set(key, val))
			return false;
		bf.add(key);
		return true;
	}

	/**
	 * Takes a key. Looks it up in BloomFilter. If key is present,
	 * finds target machine. Grabs it from Machine.
	 * @param key key
	 * @return value
	 */
	public String get(String key){
		String result = null;
		if(bf.contains(key)) {
			int machineNum = mapKeyToMachine(key);
			result = nodes[machineNum].get(key);
		}
		return result;
	}

	/**
	 * Takes an arbitrary key and returns which machine
	 * should store this key. 
	 * @param key Key
	 * @return Machine number
	 */
	private int mapKeyToMachine(String key)
	{
		return key.hashCode()%machines.length; //TODO: This doesm't take into account machine counts growing/changing over time. 
	}

}

