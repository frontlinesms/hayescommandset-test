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
		HayesState registered = HayesState.createState("ERROR: 2",
				"AT", "OK");
		HayesState initial = HayesState.createState("ERROR: 1",
				"AT", "OK",
				"AT+CREG?", r("+CREG: 1", registered));
		handler = new StatefulHayesPortHandler(initial);

		assertResponse("AT", "OK", initial);
		assertResponse("BAD_REQUEST", "ERROR: 1", initial);
		assertResponse("AT+CREG?", "+CREG: 1", initial, registered);
		assertResponse("BAD_REQUEST", "ERROR: 2", registered);
	}

	public void testRegexRequestMatchingWithStateChange() {
		HayesState pinSupplied = HayesState.createState("ERROR: 2",
				"AT", "OK",
				"AT+CPIN?", "OK");
		HayesState pinRequired = HayesState.createState("ERROR: 1",
				"AT", "OK",
				"AT+CPIN?", "SIM PIN",
				"AT+CPIN=1234", r("OK", pinSupplied),
				p("AT\\+CPIN=.*"), "ERROR: BAD PIN");
		handler = new StatefulHayesPortHandler(pinRequired);

		assertResponse("BAD_REQUEST", "ERROR: 1", pinRequired);
		assertResponse("AT+CPIN?", "SIM PIN", pinRequired);
		assertResponse("AT+CPIN=0000", "ERROR: BAD PIN", pinRequired);
		assertResponse("AT+CPIN=1234", "OK", pinRequired, pinSupplied);
		assertResponse("BAD_REQUEST", "ERROR: 2", pinSupplied);
		assertResponse("AT+CPIN?", "OK", pinSupplied);
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
		assertResponse(request, expectedResponse, expectedStateBeforeAndAfter, expectedStateBeforeAndAfter);
	}
	
	private void assertResponse(String request, String expectedResponse, HayesState expectedStartState, HayesState expectedEndState) {
		assertState(expectedStartState);
		assertResponse(request, expectedResponse);
		assertState(expectedEndState);
	}

//> STATIC HELPER METHODS
	private static HayesResponse r(String response, HayesState newState) {
		return new HayesResponse(response, newState);
	}
}
