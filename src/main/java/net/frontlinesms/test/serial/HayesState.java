package net.frontlinesms.test.serial;

import java.util.HashMap;
import java.util.Map;

public class HayesState {
	private final HayesResponse errorResponse;
	private final Map<String, HayesResponse> responses;
	
	private HayesState(HayesResponse errorResponse, Map<String, HayesResponse> responses) {
		this.errorResponse = errorResponse;
		this.responses = responses;
	}

	public HayesResponse getResponse(String request) {
		if(this.responses.containsKey(request)) {
			return this.responses.get(request);
		} else return errorResponse;
	}

	public static HayesState createState(Object errorResponse, Object... requestResponses) {
		HashMap<String, HayesResponse> responseMap = new HashMap<String, HayesResponse>();
		assert (requestResponses.length % 2) == 0;
		for (int i = 0; i < requestResponses.length; i+=2) {
			String request = (String) requestResponses[i];
			Object suppliedResponse = requestResponses[i + 1];
			HayesResponse actualResponse;
			if(suppliedResponse instanceof String) {
				actualResponse = new HayesResponse((String) suppliedResponse);
			} else actualResponse = (HayesResponse) suppliedResponse;
			responseMap.put(request, actualResponse);
		}
		return new HayesState(errorResponse instanceof HayesResponse ? (HayesResponse) errorResponse : new HayesResponse((String) errorResponse), responseMap);
	}
}
