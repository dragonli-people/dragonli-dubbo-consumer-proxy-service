package org.dragonli.service.general.dubboconsumerservice;

public class ConsumerVars {

	public static Boolean pausing = false;

	public static Boolean getPausing() {
		return pausing;
	}

	public static void setPausing(Boolean pausing) {
		ConsumerVars.pausing = pausing;
	}

	public static boolean debugLog = false;
}
