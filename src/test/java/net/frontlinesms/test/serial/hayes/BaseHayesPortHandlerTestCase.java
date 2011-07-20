package net.frontlinesms.test.serial.hayes;

import java.util.regex.Pattern;

import net.frontlinesms.junit.BaseTestCase;
import net.frontlinesms.test.serial.hayes.BaseHayesPortHandler;

public abstract class BaseHayesPortHandlerTestCase<T extends BaseHayesPortHandler> extends BaseTestCase {
	/** Handler instance under test */
	T handler;

	void assertResponse(String request, String expectedResponse) {
		assertEquals(expectedResponse, handler.getResponseText(request));
	}
	
	static Pattern p(String regex) {
		return Pattern.compile(regex);
	}
}
