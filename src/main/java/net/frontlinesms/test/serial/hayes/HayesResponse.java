package net.frontlinesms.test.serial.hayes;

public interface HayesResponse {
	String getTextResponse();
	HayesState getNextState();
}
