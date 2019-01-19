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

public class MyKeysAndValuesImplementation implements KeysAndValues {
	ErrorListener myErrorListener = null;
	final Comparator<String> myComparator = new Comparator<String>() {
		public int compare(String o1, String o2) {
			return o1.toLowerCase().compareTo(o2.toLowerCase());
		}
	};
	
	private boolean atomGroupPrepared = false;
	final private Map<String, String> innerMapper = new TreeMap<String, String>(myComparator);
	final private Set<String> AtomicGroupDefinition = new TreeSet<>();
	final private List<String> atomicGroupKeyList = new LinkedList<>();
	final private List<String> atomicGroupValueList = new LinkedList<>();

	private MyKeysAndValuesImplementation() {
		// forbidden to be called since we always want an ErrorListener
	}

	public MyKeysAndValuesImplementation(ErrorListener listener) {
		myErrorListener = listener;
		innerMapper.clear();
		AtomicGroupDefinition.clear();
		atomicGroupKeyList.clear();
		atomicGroupValueList.clear();

		this.defineAtomicGroup(new String[] { "441", "442", "500" });

	}

	private void defineAtomicGroup(String[] members) {
		for (final String member : members)
			AtomicGroupDefinition.add(member);
	}

	@Override
	public void accept(String kvPairs) {
		final String[] components = kvPairs.split(",");
		for (String component : components) {
			processSinglePair(component.trim());
		}
	}

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
		//remove the last "\n"
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
				final Integer sum = Integer.valueOf(innerValue) + Integer.valueOf(value);
				innerMapper.put(key, String.valueOf(sum));
			} else {
				// replace the old values;
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
