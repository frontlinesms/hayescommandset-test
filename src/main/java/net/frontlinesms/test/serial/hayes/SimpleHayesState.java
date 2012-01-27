package net.frontlinesms.test.serial.hayes;

import java.util.regex.Pattern;

public class SimpleHayesState implements HayesState {
	private final HayesResponse errorResponse;
	private final RegexTable<HayesResponse> responses;
	
	private SimpleHayesState(HayesResponse errorResponse, RegexTable<HayesResponse> responses) {
		this.errorResponse = errorResponse;
		this.responses = responses;
	}

	public HayesResponse getResponse(String request) {
		return responses.get(request, errorResponse);
	}
	
	public void setResponse(String request, String response) {
		this.responses.put(request, new SimpleHayesResponse(response));
	}
	
	public void setResponse(Pattern request, String response) {
		this.responses.put(request, new SimpleHayesResponse(response));
	}
	
	public void setResponse(String request, String response, HayesState newState) {
		this.responses.put(request, new SimpleHayesResponse(response, newState));
	}
	
	public void setResponse(Pattern request, String response, HayesState newState) {
		this.responses.put(request, new SimpleHayesResponse(response, newState));
	}

	public static SimpleHayesState createState(Object errorResponse, Object... requestResponses) {
		RegexTable<HayesResponse> responseMap = new RegexTable<HayesResponse>();
		assert (requestResponses.length % 2) == 0;
		for (int i = 0; i < requestResponses.length; i+=2) {
			Object request = requestResponses[i];
			Object suppliedResponse = requestResponses[i + 1];
			HayesResponse actualResponse;
			if(suppliedResponse instanceof String) {
				actualResponse = new SimpleHayesResponse((String) suppliedResponse);
			} else actualResponse = (HayesResponse) suppliedResponse;
			responseMap.put(request, actualResponse);
		}
		return new SimpleHayesState(
				errorResponse instanceof HayesResponse ? (HayesResponse) errorResponse 
						: new SimpleHayesResponse((String) errorResponse),
				responseMap);
	}
}
