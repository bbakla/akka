package ch2.futures.distributed.messages;

import java.io.Serializable;

public class KeyNotFoundException extends Exception implements Serializable{
	
	public final String key;

	public KeyNotFoundException(String key) {
		super();
		this.key = key;
	}
	
	

}
