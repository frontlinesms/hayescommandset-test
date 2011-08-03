package net.frontlinesms.test.serial.hayes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import serial.mock.SerialPortHandler;

public abstract class BaseHayesPortHandler implements SerialPortHandler {
	private final Queue<Character> responseQueue = new LinkedList<Character>(); 
	private final StringBuilder currentRequest = new StringBuilder();

	public InputStream getInputStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				Character c = responseQueue.poll();
				return c!=null? c: -1;
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
	
	private void submitCurrentRequest() {
		if (currentRequest.length() > 0) {
			String request = currentRequest.toString();
			
			addResponse(getResponseText(request));
				
			System.out.println("RECEIVED COMPLETE REQUEST: "
					+ currentRequest.toString());
			currentRequest.delete(0, Integer.MAX_VALUE);
		}
	}
	/** Get the text to respond with, and handle any associated change in state */
	protected abstract String getResponseText(String request);

	private void addResponse(String response) {
		this.responseQueue.add('\r');
		for (char c : response.toCharArray()) {
			this.responseQueue.add(c);
		}
		System.out.println("APPENDED RESPONSE: " + response);
		this.responseQueue.add('\r');
	}
	
	private boolean notLineEnd(int b) {
		// 0x1A is EOF or Ctrl-Z.  Hopefully you recognise the other characters.
		return b!='\n' && b!='\r' && b!=0x1A;
	}
}
