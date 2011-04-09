package net.frontlinesms.test.serial;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple Serial Port emulator for Hayes AT command set.  You must program the command
 * responses yourself, but standard line endings and openings will be appended for you.
 * @author aga
 *
 */
public class HayesPortHandler extends BaseHayesPortHandler {
	private final Map<String, String> requestAndResponseMap = new HashMap<String, String>();
	private final String unknownResponse;
	
	public HayesPortHandler(String unknownResponse, String... requestsAndResponses) {
		assert(requestsAndResponses.length % 1 == 0): "Must have an even number of requests and responses.";
		for (int i = 0; i < requestsAndResponses.length; i+=2) {
			String request = requestsAndResponses[i];
			assert(request != null);
			String response = requestsAndResponses[i+1];
			assert(response != null);
			
			String replaced = requestAndResponseMap.put(request, response);
			assert(replaced != null);
		}

		assert unknownResponse != null;
		this.unknownResponse = unknownResponse;
	}

	@Override
	protected String getResponseText(String request) {
		String response = requestAndResponseMap.get(request);			
		if(response != null) {
			return response;
		} else {
			return unknownResponse;
		}
	}
}
