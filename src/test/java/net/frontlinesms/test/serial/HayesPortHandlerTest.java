package net.frontlinesms.test.serial;

import net.frontlinesms.junit.BaseTestCase;

/** unit tests for {@link HayesPortHandler} */
public class HayesPortHandlerTest extends BaseTestCase {
	/** {@link HayesPortHandler} instance under test */
	private HayesPortHandler handler;
	
	public void testGetResponseText_plaintext() {
		handler = new HayesPortHandler("ERROR",
				"A-query", "A-response");
		assertResponseEquals("A-response", "A-query");
		assertResponseEquals("ERROR", "Not-specified");
	}
	
	public void testGetResponse_regex() {
		handler = new HayesPortHandler("ERROR",
				"AT+CMGS=\\d+", "OK" /* send message size*/
				);
		assertResponseEquals("OK", "AT+CMGS=112");
		assertResponseEquals("ERROR", "AT+CMGS=abc");
	}
	
	public void testGetResponse_regexPriorityA() {
		handler = new HayesPortHandler("ERROR",
				"AT+CMGS=\\d+", "OK",
				"AT+CMGS=112", "OMG!");
		assertResponseEquals("AT+CMGS=112", "OK");
	}
	
	public void testGetResponse_regexPriorityB() {
		handler = new HayesPortHandler("ERROR",
				"AT+CMGS=112", "OMG!",
				"AT+CMGS=\\d+", "OK");
		assertResponseEquals("AT+CMGS=112", "OMG!");
	}

	private void assertResponseEquals(String expectedResponse, String query) {
		assertEquals(expectedResponse, handler.getResponseText(query));
	}
}
