package net.frontlinesms.test.serial.hayes;

public class HayesResponse {
	private final String textResponse;
	private final HayesState nextState;

	public HayesResponse(String textResponse) {
		this(textResponse, null);
	}
	
	public String getTextResponse() {
		return textResponse;
	}
	
	public HayesState getNextState() {
		return nextState;
	}

	public HayesResponse(String textResponse, HayesState nextState) {
		this.textResponse = textResponse;
		this.nextState = nextState;
	}
}