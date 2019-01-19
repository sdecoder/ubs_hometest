package org.hometest.ubs.ubs_hometest;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

public class MyKeysAndValuesImplementation implements KeysAndValues {

	/*
	 * Requirement: Provide a way of injecting an ErrorListener into your
	 * KeysAndValues implementation. Do not create a global instance. Solution: use
	 * the bean configuration of the injection mechanism by Spring framework to
	 * fulfill this requirement
	 */
	final private ErrorListener myErrorListener;

	final Comparator<String> customizedComparator = new Comparator<String>() {
		public int compare(String o1, String o2) {
			return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	};

	private boolean atomGroupPrepared = false;
	final private Map<String, String> innerMapper = new TreeMap<String, String>(customizedComparator);
	final private Set<String> AtomicGroupDefinition = new TreeSet<>();
	final private List<String> atomicGroupKeyList = new LinkedList<>();
	final private List<String> atomicGroupValueList = new LinkedList<>();

	@Autowired
	public MyKeysAndValuesImplementation(ErrorListener myErrorListener) {
		innerMapper.clear();
		AtomicGroupDefinition.clear();
		atomicGroupKeyList.clear();
		atomicGroupValueList.clear();
		this.myErrorListener = myErrorListener;

		this.defineAtomicGroup(new String[] { "441", "442", "500" });
	}

	private void defineAtomicGroup(String[] members) {
		for (final String member : members)
			AtomicGroupDefinition.add(member);
	}

	/*
	 * Required API implementation: void accept(String kvPairs);
	 */
	@Override
	public void accept(String kvPairs) {
		final String[] components = kvPairs.split(",");
		for (String component : components) {
			processSinglePair(component.trim());
		}
	}

	/*
	 * Required API implementation: String display();
	 */
	@Override
	public String display() {
		validateAtomicGroup();

		StringBuilder sBuilder = new StringBuilder();
		for (String key : innerMapper.keySet()) {
			sBuilder.append(key);
			sBuilder.append("=");
			sBuilder.append(innerMapper.get(key));
			sBuilder.append("\n");
		}
		// remove the last "\n"
		return sBuilder.substring(0, sBuilder.length() - 1);
	}

	private void validateAtomicGroup() {
		if (atomicGroupKeyList.size() == 0)
			return;
		if (atomicGroupKeyList.size() < AtomicGroupDefinition.size()) {
			final Set<String> missing = new HashSet<>();
			missing.addAll(AtomicGroupDefinition);
			missing.removeAll(atomicGroupKeyList);

			myErrorListener.onIncompleteAtomicGroup(AtomicGroupDefinition, missing);
			if (!atomGroupPrepared)
				// since the AtomicGroup is NOT complete, so we remove the partial keys;
				innerMapper.keySet().removeAll(atomicGroupKeyList);
		}
	}

	private boolean isCompleteAtomGroup(List<String> testObj) {
		Set<String> testSet = new HashSet<String>(testObj);
		if (testSet.size() != AtomicGroupDefinition.size())
			return false;
		if (AtomicGroupDefinition.containsAll(testSet))
			return true;
		return false;
	}

	private void processSinglePair(final String kvPair) {

		final String[] components = kvPair.split("=");
		if (components.length != 2) {
			myErrorListener.onError("component format error!");
			return;
		}

		final int keyIndex = 0;
		final int valueIndx = 1;
		final String key = components[keyIndex].trim();
		final String value = components[valueIndx].trim();

		// detect the atomic group:
		if (AtomicGroupDefinition.size() != 0 && AtomicGroupDefinition.contains(key)) {
			atomicGroupKeyList.add(key);
			atomicGroupValueList.add(value);
			if (atomicGroupKeyList.size() == AtomicGroupDefinition.size()) {
				if (isCompleteAtomGroup(atomicGroupKeyList)) {

					atomGroupPrepared = true;
					for (int i = 0; i < AtomicGroupDefinition.size(); i++) {
						final String atomKey = atomicGroupKeyList.get(i);
						final String atomValue = atomicGroupValueList.get(i);
						updateKV(atomKey, atomValue);
					}
				} else {
					// this is overlapped case:
					myErrorListener.onError("keys within the same group cannot 'overlap'");
				}
				atomicGroupKeyList.clear();
				atomicGroupValueList.clear();
			}
		} else {
			// update other kv pair types
			updateKV(key, value);
		}
	}

	private void updateKV(final String key, final String value) {
		if (!innerMapper.containsKey(key)) {
			innerMapper.put(key, value);
		} else {

			final String innerValue = innerMapper.get(key);
			if (isNumeric(value) && isNumeric(innerValue)) {
				// numeric integer values accumulate
				final Integer sum = Integer.valueOf(innerValue) + Integer.valueOf(value);
				innerMapper.put(key, String.valueOf(sum));
			} else {
				// non-integers overwrite: replace the old values
				innerMapper.put(key, value);
			}
		}
	}

	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("-?^[0-9]*$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
