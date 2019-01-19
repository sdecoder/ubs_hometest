package org.hometest.ubs.ubs_hometest;

import org.apache.commons.lang.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest extends TestCase {

	public AppTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	final private ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
	final private MyKeysAndValuesImplementation kv = (MyKeysAndValuesImplementation) context
			.getBean(MyKeysAndValuesImplementation.class);

	public void test01() {
		kv.accept("pi=314159,hello=world");
		final String result = kv.display();
		final String exceptedAnswer = "hello=world\n" + "pi=314159";
		assertEquals(result, exceptedAnswer);
	}

	public void test02() {
		kv.accept("14=15, 14=7,A=B52, 14 = 4, dry = Don't Repeat Yourself");
		final String result = kv.display();
		final String exceptedAnswer = "14=26\n" + "A=B52\n" + "dry=Don't Repeat Yourself";
		assertEquals(result, exceptedAnswer);
	}

	public void test03() {
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

	public void test04() {
		kv.accept("14=X");
		kv.accept("5=6");
		kv.accept("one=two");
		kv.accept("Three=four");
		final String result = kv.display();
		final String exceptedAnswer = "14=X\n" + "5=6\n" + "one=two\n" + "Three=four";
		assertEquals(result, exceptedAnswer);
	}

	public void test05() {
		kv.accept("441=one,X=Y, 442=2,500=three");
		final String result = kv.display();
		final String exceptedAnswer = "441=one\n" + "442=2\n" + "500=three\n" + "X=Y";
		assertEquals(result, exceptedAnswer);
	}

	public void test06() {
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

	public void test07() {
		kv.accept("441=3,500=not ok,13=qwerty");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("13=qwerty");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test08() {
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

	public void test09() {
		kv.accept("one=two");
		kv.accept("Three=four");
		kv.accept("5=6");
		kv.accept("14=X");
		final String result = kv.display();

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("14=X\n");
		stringBuilder.append("5=6\n");
		stringBuilder.append("one=two\n");
		stringBuilder.append("Three=four");

		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test10() {
		kv.accept(null);
		final String result = kv.display();
		assertEquals(result, StringUtils.EMPTY);
	}

	public void test11() {
		kv.accept("441=1, 442=1, 441=2, 500=1, 442=2, 500=2");
		final String result = kv.display();
		assertEquals(result, StringUtils.EMPTY);
	}

	public void test12() {
		kv.accept("441=1====123123=123=1=23");
		final String result = kv.display();
		assertEquals(result, StringUtils.EMPTY);
	}

	public void test13() {
		kv.accept("441=1, x=y ");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("x=y");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test14() {
		kv.accept(" x=y    , x =  5");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("x=5");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test15() {
		kv.accept(" x=y    , x =  z");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("x=z");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test16() {
		kv.accept(" x=5    , x =  z");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("x=z");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test17() {
		kv.accept(" x=5    , x =  100           ,");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("x=105");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test18() {
		kv.accept("  = ,  =  ,  =  ,,,,,,,,,,,,,,,,,,,,,,,,,");
		final String result = kv.display();
		assertEquals(result, StringUtils.EMPTY);
	}

	public void test19() {
		kv.accept("x= ,");
		final String result = kv.display();
		assertEquals(result, StringUtils.EMPTY);
	}

	public void test20() {
		kv.accept("441=1, 442=1,  500=1, 441=2, 442=2, 500=2");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("441=3\n");
		stringBuilder.append("442=3\n");
		stringBuilder.append("500=3");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test21() {
		kv.accept("441=A, 442=1,  500=1, 441=2, 442=B, 500=2");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("441=2\n");
		stringBuilder.append("442=B\n");
		stringBuilder.append("500=3");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test22() {
		kv.accept("441=A, 442=1,  500=1, 441=2, 442=B, 500=2");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("441=2\n");
		stringBuilder.append("442=B\n");
		stringBuilder.append("500=3");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test23() {
		kv.accept("441=A, 442=1,  500=1, 442=B, 500=2, 441=2");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("441=2\n");
		stringBuilder.append("442=B\n");
		stringBuilder.append("500=3");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test24() {
		kv.accept("441=A, 442=1,  500=1, 442=B, 500=2, 441=2, 500=AAA ");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("441=2\n");
		stringBuilder.append("442=B\n");
		stringBuilder.append("500=3");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test25() {
		kv.accept("1=X, Y=TTT, Z =???!!!, 441=A, 442=1,  500=1, 442=B, 500=2, 441=2, 500=AAA ");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("1=X\n");
		stringBuilder.append("441=2\n");
		stringBuilder.append("442=B\n");
		stringBuilder.append("500=3\n");
		stringBuilder.append("Y=TTT\n");
		stringBuilder.append("Z=???!!!");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}

	public void test26() {
		kv.accept("Y=TTT,  441=A,500=1, 1=X,  442=1,Z =???!!!,   442=B, ttt=***, 500=2, 441=2, 500=AAA ");
		final String result = kv.display();
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("1=X\n");
		stringBuilder.append("441=2\n");
		stringBuilder.append("442=B\n");
		stringBuilder.append("500=3\n");
		stringBuilder.append("ttt=***\n");
		stringBuilder.append("Y=TTT\n");
		stringBuilder.append("Z=???!!!");
		final String exceptedAnswer = stringBuilder.toString();
		assertEquals(result, exceptedAnswer);
	}
}
