package net.frontlinesms.test.serial.hayes;

import net.frontlinesms.test.serial.hayes.SimpleHayesResponse;
import net.frontlinesms.test.serial.hayes.HayesState;
import net.frontlinesms.test.serial.hayes.StatefulHayesPortHandler;

/** Unit tests for {@link StatefulHayesPortHandler} */
public class StatefulHayesPortHandlerTest extends BaseHayesPortHandlerTestCase<StatefulHayesPortHandler> {	
	public void testStandardRequestMatching() {
		HayesState state = SimpleHayesState.createState("ERROR", 
				"AT", "OK");
		handler = new StatefulHayesPortHandler(state);
		
		assertResponse("BAD_REQUEST", "ERROR", state);
		assertResponse("AT", "OK");
	}

	public void testRegexRequestMatching() {
		HayesState state = SimpleHayesState.createState("ERROR", 
				p("AT\\+CMGS=\\d+"), "OK");
		handler = new StatefulHayesPortHandler(state);
		
		assertResponse("AT+CMGS=112", "OK", state);
	}
	
	public void testRegexRequestMatchingWithMultiplePossibleMatchesA() {
		HayesState state = SimpleHayesState.createState("ERROR",
				p("AT\\+CMGS=\\d+"), "OK",
				"AT+CMGS=112", "OMG!");
		handler = new StatefulHayesPortHandler(state);
		
		assertResponse("AT+CMGS=112", "OK", state);
		assertResponse("AT+CMGS=abc", "ERROR", state);
	}
	
	public void testRegexRequestMatchingWithMultiplePossibleMatchesB() {
		HayesState state = SimpleHayesState.createState("ERROR",
				"AT+CMGS=112", "OMG!",
				p("AT\\+CMGS=\\d+"), "OK");
		handler = new StatefulHayesPortHandler(state);

		assertResponse("AT+CMGS=112", "OMG!", state);
		assertResponse("AT+CMGS=123", "OK", state);
		assertResponse("AT+CMGS=abc", "ERROR", state);
	}
	
	public void testStandardRequestMatchingWithStateChange() {
		HayesState registered = SimpleHayesState.createState("ERROR: 2",
				"AT", "OK");
		HayesState initial = SimpleHayesState.createState("ERROR: 1",
				"AT", "OK",
				"AT+CREG?", r("+CREG: 1", registered));
		handler = new StatefulHayesPortHandler(initial);

		assertResponse("AT", "OK", initial);
		assertResponse("BAD_REQUEST", "ERROR: 1", initial);
		assertResponse("AT+CREG?", "+CREG: 1", initial, registered);
		assertResponse("BAD_REQUEST", "ERROR: 2", registered);
	}

	public void testRegexRequestMatchingWithStateChange() {
		HayesState pinSupplied = SimpleHayesState.createState("ERROR: 2",
				"AT", "OK",
				"AT+CPIN?", "OK");
		HayesState pinRequired = SimpleHayesState.createState("ERROR: 1",
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
	
	public void testErrorRequestMatchingWithStateChange() {
		HayesState bad = SimpleHayesState.createState("+CMS ERROR: 999");
		HayesState good = SimpleHayesState.createState(r("ERROR", bad),
				"AT", "OK");
		handler = new StatefulHayesPortHandler(good);

		assertResponse("AT", "OK", good);
		assertResponse("BAD_REQUEST", "ERROR", good, bad);
		assertResponse("AT", "+CMS ERROR: 999", bad);
		assertResponse("BAD_REQUEST", "+CMS ERROR: 999", bad);
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
	private static SimpleHayesResponse r(String response, HayesState newState) {
		return new SimpleHayesResponse(response, newState);
	}
}
