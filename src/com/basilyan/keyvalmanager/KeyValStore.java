package com.basilyan.keyvalmanager;
import java.net.InetAddress;

/**
 * This abstracts away a distributed KeyVal store.
 * It has a list of machines that can be queried for a key and will return
 * the value if they have it. 
 * 
 * Uses a BloomFilter to avoid unnecessary look ups. 
 * 
 * @author Michael Basilyan
 *
 */
public class KeyValStore {
	private BloomFilter<String> bf;
	private double pFalsePos = 0.0001;
	private KeyValAbstractNode machines[];
	
	public KeyValStore(int numItems, InetAddress[] machines)
	{
		bf = new BloomFilter<String>(pFalsePos, numItems);
		for(int i = 0; i < machines.length; i++)
		{
			this.machines[i] = new KeyValAbstractNode(machines[i]);
		}
	}
	
	/**
	 * Adds a key-val pair to the distributed store.
	 * @param key Key
	 * @param val Value
	 * @return true for success; false for failure
	 */
	public boolean add(String key, String val)
	{
		int machineNum = mapKeyToMachine(key);
		if(!machines[machineNum].set(key, val))
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
			result = machines[machineNum].get(key);
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
		return key.hashCode()%machines.length;
	}
	
	
}
