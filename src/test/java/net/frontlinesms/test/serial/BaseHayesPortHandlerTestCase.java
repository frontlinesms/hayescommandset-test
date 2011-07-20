package net.frontlinesms.test.serial;

import java.util.regex.Pattern;

import net.frontlinesms.junit.BaseTestCase;

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
