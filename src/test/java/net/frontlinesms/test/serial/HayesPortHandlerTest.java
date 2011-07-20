package net.frontlinesms.test.serial;

/** unit tests for {@link HayesPortHandler} */
public class HayesPortHandlerTest extends BaseHayesPortHandlerTestCase<HayesPortHandler> {
	public void testGetResponseText_plaintext() {
		handler = new HayesPortHandler("ERROR",
				"A-query", "A-response");
		assertResponse("A-query", "A-response");
		assertResponse("Not-specified", "ERROR");
	}
	
	public void testGetResponse_regex() {
		handler = new HayesPortHandler("ERROR",
				p("AT\\+CMGS=\\d+"), "OK" /* send message size*/);
		assertResponse("AT+CMGS=112", "OK");
		assertResponse("AT+CMGS=abc", "ERROR");
	}
	
	public void testGetResponse_regexPriorityA() {
		handler = new HayesPortHandler("ERROR",
				p("AT\\+CMGS=\\d+"), "OK",
				"AT+CMGS=112", "OMG!");
		assertResponse("AT+CMGS=112", "OK");
	}
	
	public void testGetResponse_regexPriorityB() {
		handler = new HayesPortHandler("ERROR",
				"AT+CMGS=112", "OMG!",
				p("AT\\+CMGS=\\d+"), "OK");
		assertResponse("AT+CMGS=112", "OMG!");
	}
}
