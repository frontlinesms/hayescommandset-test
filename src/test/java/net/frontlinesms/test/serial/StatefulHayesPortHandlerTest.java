package net.frontlinesms.test.serial;

/** Unit tests for {@link StatefulHayesPortHandler} */
public class StatefulHayesPortHandlerTest extends BaseHayesPortHandlerTestCase<StatefulHayesPortHandler> {	
	public void testStandardRequestMatching() {
		HayesState state = HayesState.createState("ERROR", 
				"AT", "OK");
		handler = new StatefulHayesPortHandler(state);
		
		assertResponse("BAD_REQUEST", "ERROR", state);
		assertResponse("AT", "OK");
	}

	public void testRegexRequestMatching() {
		HayesState state = HayesState.createState("ERROR", 
				p("AT\\+CMGS=\\d+"), "OK");
		handler = new StatefulHayesPortHandler(state);
		
		assertResponse("AT+CMGS=112", "OK", state);
	}
	
	public void testRegexRequestMatchingWithMultiplePossibleMatchesA() {
		HayesState state = HayesState.createState("ERROR",
				p("AT\\+CMGS=\\d+"), "OK",
				"AT+CMGS=112", "OMG!");
		handler = new StatefulHayesPortHandler(state);
		
		assertResponse("AT+CMGS=112", "OK", state);
		assertResponse("AT+CMGS=abc", "ERROR", state);
	}
	
	public void testRegexRequestMatchingWithMultiplePossibleMatchesB() {
		HayesState state = HayesState.createState("ERROR",
				"AT+CMGS=112", "OMG!",
				p("AT\\+CMGS=\\d+"), "OK");
		handler = new StatefulHayesPortHandler(state);

		assertResponse("AT+CMGS=112", "OMG!", state);
		assertResponse("AT+CMGS=123", "OK", state);
		assertResponse("AT+CMGS=abc", "ERROR", state);
	}
	
	public void testStandardRequestMatchingWithStateChange() {
		fail("No tests written yet.");
	}
	
	public void testRegexRequestMatchingWithStateChange() {
		fail("No tests written yet.");
	}
	
	public void testStandardErrorRequestMatchingWithStateChange() {
		fail("No tests written yet.");
	}
	
	public void testRegexErrorRequestMatchingWithStateChange() {
		fail("No tests written yet.");
	}

//> TEST HELPER METHODS
	private void assertState(HayesState expectedState) {
		assertEquals(expectedState, handler.getCurrentState());
	}
	
	private void assertResponse(String request, String expectedResponse, HayesState expectedStateBeforeAndAfter) {
		assertState(expectedStateBeforeAndAfter);
		assertResponse(request, expectedResponse);
		assertState(expectedStateBeforeAndAfter);
	}
}
