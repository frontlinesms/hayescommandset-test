package net.frontlinesms.test.serial.hayes;

/**
 * Simple Serial Port emulator for Hayes AT command set.  You must program the command
 * responses yourself, but standard line endings and openings will be appended for you.
 * @author aga
 *
 */
public class HayesPortHandler extends BaseHayesPortHandler {
	private RegexTable<String> requestAndResponseTable = new RegexTable<String>();
	private final String unknownResponse;
	
	public HayesPortHandler(String unknownResponse, Object... requestsAndResponses) {
		assert(requestsAndResponses.length % 1 == 0): "Must have an even number of requests and responses.";
		for (int i = 0; i < requestsAndResponses.length; i+=2) {
			Object request = requestsAndResponses[i];
			assert(request != null);
			String response = (String) requestsAndResponses[i+1];
			assert(response != null);
			
			String replaced = requestAndResponseTable.put(request, response);
			assert(replaced == null);
		}

		assert unknownResponse != null;
		this.unknownResponse = unknownResponse;
	}

	@Override
	protected String getResponseText(String request) {
		return requestAndResponseTable.get(request, unknownResponse);
	}
}
