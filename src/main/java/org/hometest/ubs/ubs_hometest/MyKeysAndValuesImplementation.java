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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import Protocols.ErrorListener;
import Protocols.KeysAndValues;

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
	final private Map<String, String> innerMapper = new TreeMap<>(customizedComparator);
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
		try {
			for (final String member : members)
				AtomicGroupDefinition.add(member);
		} catch (Exception e) {
			myErrorListener.onError("Exception found on defining the AtomicGroup: " + members.toString(), e);
		}
	}

	/*
	 * Required API implementation: void accept(String kvPairs);
	 */
	@Override
	public void accept(String kvPairs) {
		if (kvPairs == null) {
			this.myErrorListener.onError("found null input string, ignore...");
			return;
		}
		try {
			final String[] components = kvPairs.split(",");
			for (String kvpair : components) {
				processSinglePair(kvpair.trim());
			}
		} catch (Exception e) {
			myErrorListener.onError("Exception found on accepting kvPairs input: " + kvPairs, e);
		}
	}

	/*
	 * Required API implementation: String display();
	 */
	@Override
	public String display() {
		try {
			validateAtomicGroup();

			StringBuilder sBuilder = new StringBuilder();
			if (innerMapper.size() == 0)
				return StringUtils.EMPTY;

			for (String key : innerMapper.keySet()) {
				sBuilder.append(key);
				sBuilder.append("=");
				sBuilder.append(innerMapper.get(key));
				sBuilder.append("\n");
			}
			// remove the last "\n"
			sBuilder.setLength(sBuilder.length() - 1);
			return sBuilder.toString();
		} catch (Exception e) {
			myErrorListener.onError("Error occurs when displaying the result", e);
		}
		return StringUtils.EMPTY;
	}

	private void validateAtomicGroup() {
		try {
			if (atomicGroupKeyList.size() == 0)
				return;

			if (atomicGroupKeyList.size() < AtomicGroupDefinition.size()) {
				final Set<String> missing = new HashSet<>();
				missing.addAll(AtomicGroupDefinition);
				missing.removeAll(atomicGroupKeyList);

				myErrorListener.onIncompleteAtomicGroup(AtomicGroupDefinition, missing);
				if (!atomGroupPrepared)
					/*
					 * since the AtomicGroup is NOT complete, so we remove the partial keys; that
					 * is, partial keys are NOT allowed to be exhibitted to the user;
					 */
					innerMapper.keySet().removeAll(atomicGroupKeyList);
			}
		} catch (Exception e) {
			myErrorListener.onError("Error occurs when validating the AtomicGroup", e);
		}
	}

	private boolean isCompleteAtomGroup(List<String> testList) {
		try {
			final Set<String> testSet = new HashSet<String>(testList);
			if (testSet.size() != AtomicGroupDefinition.size())
				return false;
			if (AtomicGroupDefinition.containsAll(testSet))
				return true;
		} catch (Exception e) {
			myErrorListener.onError("Error occurs when checking if the AtomicGroup is complete: " + testList.toString(), e);
		}
		return false;
	}

	private void processSinglePair(final String kvPair) {
		try {
			/*
			 * don't allow there exist more than one =
			 */
			final String[] components = kvPair.split("=");
			if (components.length != 2) {
				myErrorListener.onError("component format error for this key-value pair: " + kvPair);
				return;
			}

			final int keyIndex = 0;
			final int valueIndx = 1;
			final String key = components[keyIndex].trim();
			final String value = components[valueIndx].trim();
			if (key.equals(StringUtils.EMPTY)) {
				myErrorListener.onError("empty key found for this key-value pair: " + kvPair);
				return;
			}

			if (value.equals(StringUtils.EMPTY)) {
				myErrorListener.onError("empty value found for this key-value pair: " + kvPair);
				return;
			}

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
						/*
						 * We detected a group but it contains 2+ duplicated elements This is treated as
						 * the overlap issue;
						 */
						myErrorListener.onError("keys within the same group cannot 'overlap': " + atomicGroupKeyList);
					}
					atomicGroupKeyList.clear();
					atomicGroupValueList.clear();
				}
			} else {
				// update other kv pair types
				updateKV(key, value);
			}
		} catch (Exception e) {
			myErrorListener.onError("Error occurs when processing the single KV pair: " + kvPair, e);
		}
	}

	private void updateKV(final String key, final String value) {

		try {
			if (key == null || value == null)
				return;
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
		} catch (Exception e) {
			final String errStr = "Exception found when updating the KV pair: [" + key + "," + value + "]";
			myErrorListener.onError(errStr, e);
		}
	}

	private boolean isNumeric(String str) {
		try {
			Pattern pattern = Pattern.compile("-?^[0-9]*$");
			Matcher isNum = pattern.matcher(str);
			if (!isNum.matches()) {
				return false;
			}
			return true;
		} catch (Exception e) {
			myErrorListener.onError("Exception found when validating number string: " + str, e);
		}
		return false;
	}
}
