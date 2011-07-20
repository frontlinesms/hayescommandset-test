package net.frontlinesms.test.serial.hayes;

public class StatefulHayesPortHandler extends BaseHayesPortHandler {
	private HayesState currentState;
	
	public StatefulHayesPortHandler(HayesState initialState) {
		this.currentState = initialState;
	}
	
	@Override
	protected String getResponseText(String request) {
		HayesResponse response = currentState.getResponse(request);
		
		HayesState newState = response.getNextState();
		if(newState != null) {
			this.currentState = newState;
		}
		
		return response.getTextResponse();
	}

	public Object getCurrentState() {
		return currentState;
	}
}
