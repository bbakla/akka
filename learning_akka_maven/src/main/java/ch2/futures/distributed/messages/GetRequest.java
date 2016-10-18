package ch2.futures.distributed.messages;

public class GetRequest {

	private final String key;

	public GetRequest(String key) {
		super();
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	
	
}
