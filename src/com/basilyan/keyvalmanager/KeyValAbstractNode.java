package com.basilyan.keyvalmanager;
import java.net.InetAddress;

/**
 * Abstracts away the notion of a key-value machine.
 * Each instance of this class corresponds to a single KV machine.
 * For each machine we store an ip address.
 * 
 * @author Michael Basilyan
 *
 */
public class KeyValAbstractNode {

	public InetAddress ip;
	
	public KeyValAbstractNode(InetAddress ip)
	{
			this.ip = ip;
	}
	
	/**
	 * Sets the value for a given key on this machine. 
	 * @param key key
	 * @param val value
	 * @return true for success, false for failure
	 */
	public boolean set(String key, String val)
	{
		//TODO: Ping actual machine and set value. 
		return false;
	}
	
	/**
	 * Gets the value from this machine for the provided key
	 * @param key key
	 * @return value
	 */
	public String get(String key)
	{
			//TODO: Ping actual machine and get value.
			return "";
	}
}
