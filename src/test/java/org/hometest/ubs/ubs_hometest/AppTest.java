package org.hometest.ubs.ubs_hometest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	MyErrorListener errorListener = new MyErrorListener();
	final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);

	public void test1() {
		kv.accept("pi=314159,hello=world");
		final String result = kv.display();
		final String exceptedAnswer = "hello=world\n" + "pi=314159";
		assertEquals(result, exceptedAnswer);
	}

	public void test2() {
		kv.accept("14=15, 14=7,A=B52, 14 = 4, dry = Don't Repeat Yourself");
		final String result = kv.display();
		final String exceptedAnswer = "14=26\n" + "A=B52\n" + "dry=Don't Repeat Yourself";
		assertEquals(result, exceptedAnswer);
	}

	public void test3() {
		kv.accept("14=15");
		kv.accept("A=B52");		
		kv.accept("dry=D.R.Y.");
		kv.accept("14=7");
		kv.accept("14=4");
		kv.accept("dry=Don't Repeat Yourself");
		final String result = kv.display();
		final String exceptedAnswer = "14=26\n" + "A=B52\n" + "dry=Don't Repeat Yourself";
		assertEquals(result, exceptedAnswer);
	}	
	
	public void test4() {
		kv.accept("14=X");
		kv.accept("5=6");		
		kv.accept("one=two");
		kv.accept("Three=four");		
		final String result = kv.display();
		final String exceptedAnswer = "14=X\n" + "5=6\n" + "one=two\n" + "Three=four";
		assertEquals(result, exceptedAnswer);
	}
	
	public void test5() {
		kv.accept("441=one,X=Y, 442=2,500=three");
		final String result = kv.display();
		final String exceptedAnswer = "441=one\n" + "442=2\n" + "500=three\n" + "X=Y";
		assertEquals(result, exceptedAnswer);
	}
	
	public void test6() {
		kv.accept("18=zzz,441=one,500=three,442=2,442= A,441 =3,35=D,500=ok  ");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("18=zzz\n");
		stringBuilder.append("35=D\n");
		stringBuilder.append("441=3\n");
		stringBuilder.append("442=A\n");
		stringBuilder.append("500=ok");		
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}
	
	public void test7() {
		kv.accept("441=3,500=not ok,13=qwerty");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("13=qwerty");		
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}
	
	public void test8() {
		kv.accept("500= three , 6 = 7 ,441= one,442=1,442=4");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("441=one\n");
		stringBuilder.append("442=1\n");
		stringBuilder.append("500=three\n");
		stringBuilder.append("6=7");		
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

}
