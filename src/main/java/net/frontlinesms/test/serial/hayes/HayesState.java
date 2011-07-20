package net.frontlinesms.test.serial.hayes;

import java.util.regex.Pattern;

public class HayesState {
	private final HayesResponse errorResponse;
	private final RegexTable<HayesResponse> responses;
	
	private HayesState(HayesResponse errorResponse, RegexTable<HayesResponse> responses) {
		this.errorResponse = errorResponse;
		this.responses = responses;
	}

	public HayesResponse getResponse(String request) {
		return responses.get(request, errorResponse);
	}
	
	public void setResponse(String request, String response) {
		this.responses.put(request, new HayesResponse(response));
	}
	
	public void setResponse(Pattern request, String response) {
		this.responses.put(request, new HayesResponse(response));
	}
	
	public void setResponse(String request, String response, HayesState newState) {
		this.responses.put(request, new HayesResponse(response, newState));
	}
	
	public void setResponse(Pattern request, String response, HayesState newState) {
		this.responses.put(request, new HayesResponse(response, newState));
	}

	public static HayesState createState(Object errorResponse, Object... requestResponses) {
		RegexTable<HayesResponse> responseMap = new RegexTable<HayesResponse>();
		assert (requestResponses.length % 2) == 0;
		for (int i = 0; i < requestResponses.length; i+=2) {
			Object request = requestResponses[i];
			Object suppliedResponse = requestResponses[i + 1];
			HayesResponse actualResponse;
			if(suppliedResponse instanceof String) {
				actualResponse = new HayesResponse((String) suppliedResponse);
			} else actualResponse = (HayesResponse) suppliedResponse;
			responseMap.put(request, actualResponse);
		}
		return new HayesState(
				errorResponse instanceof HayesResponse ? (HayesResponse) errorResponse 
						: new HayesResponse((String) errorResponse),
				responseMap);
	}
}
