package net.frontlinesms.test.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import serial.mock.SerialPortHandler;

/**
 * Simple Serial Port emulator for Hayes AT command set.  You must program the command
 * responses yourself, but standard line endings and openings will be appended for you.
 * @author aga
 *
 */
public class HayesPortHandler implements SerialPortHandler {
	private final Map<String, String> requestAndResponseMap = new HashMap<String, String>();
	private final Queue<Character> responseQueue = new LinkedList<Character>(); 
	private final StringBuilder currentRequest = new StringBuilder();
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

		this.unknownResponse = unknownResponse;
	}

	public InputStream getInputStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				Character c = responseQueue.poll();
				if(c != null) return c;
				else return -1;
			}
			@Override
			public int available() throws IOException {
				return responseQueue.size();
			}
		};
	}

	public OutputStream getOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				if(notLineEnd(b)) {
					currentRequest.append((char) b);
				} else {
					submitCurrentRequest();
				}
			}
		};
	}
	
	private boolean notLineEnd(int b) {
		return b != '\n' && b != '\r';
	}

	private void submitCurrentRequest() {
		if(currentRequest.length() > 0) {
			String request = currentRequest.toString();
			String response = requestAndResponseMap.get(request);
			if(response != null) {
				addResponse(response);
			} else if(unknownResponse != null) {
				addResponse(unknownResponse);
			}
			System.out.println("RECEIVED COMPLETE REQUEST: " + currentRequest.toString());
			currentRequest.delete(0, Integer.MAX_VALUE);
		}
	}
	
	private void addResponse(String response) {
		this.responseQueue.add('\r');
		for(char c : response.toCharArray()) {
			this.responseQueue.add(c);
		}
		System.out.println("APPENDED RESPONSE: " + response);
		this.responseQueue.add('\r');
	}
}
