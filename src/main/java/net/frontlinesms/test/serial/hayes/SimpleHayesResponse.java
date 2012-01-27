package net.frontlinesms.test.serial.hayes;

public class SimpleHayesResponse implements HayesResponse {
	private final String textResponse;
	private final HayesState nextState;

	public SimpleHayesResponse(String textResponse) {
		this(textResponse, null);
	}
	
	public String getTextResponse() {
		return textResponse;
	}
	
	public HayesState getNextState() {
		return nextState;
	}

	public SimpleHayesResponse(String textResponse, HayesState nextState) {
		this.textResponse = textResponse;
		this.nextState = nextState;
	}
}