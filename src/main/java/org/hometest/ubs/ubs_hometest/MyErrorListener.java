package org.hometest.ubs.ubs_hometest;
import java.util.Set;

public class MyErrorListener implements ErrorListener {
	public void onError(String msg) {
		System.err.println("[ERROR] Message: " + msg);
	}

	public void onError(String msg, Exception e) {
		System.err.println("[ERROR] Message: " + msg);
		e.printStackTrace();
	}

	public void onIncompleteAtomicGroup(Set<String> group, Set<String> missing) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[ERROR] Incomplete Atomic Group found for complete group: ");
		stringBuilder.append(group.toString());
		stringBuilder.append(" missing: ");
		stringBuilder.append(missing.toString());
		System.err.println(stringBuilder.toString());		
	}
}
