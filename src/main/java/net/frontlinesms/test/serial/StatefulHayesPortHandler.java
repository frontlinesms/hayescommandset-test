package net.frontlinesms.test.serial;

public class StatefulHayesPortHandler extends BaseHayesPortHandler {
	private HayesState currentState;
	
	@Override
	protected String getResponseText(String request) {
		HayesResponse response = currentState.getResponse(request);
		
		HayesState newState = response.getNextState();
		if(newState != null) {
			this.currentState = newState;
		}
		
		return response.getTextResponse();
	}
}
