package org.hometest.ubs.ubs_hometest;
import java.util.TreeSet;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyErrorListener errorListener = new MyErrorListener();
		MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);




	}

	private static String testCase1(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("one=two");
		kv.accept("Three=four");
		kv.accept("5=6");
		kv.accept("14=X");
		return kv.display();
	}

	private static String testCase2(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("14=15");
		kv.accept("A=B52");
		kv.accept("dry=D.R.Y.");
		kv.accept("14=7");
		kv.accept("14=4");
		kv.accept("dry=Don't Repeat Yourself");
		return kv.display();
	}

	private static String testCase3(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("14=15, A=B52, dry=D.R.Y., 14=7, 14=4, dry=Don't Repeat Yourself");
		return kv.display();
	}
	
	private static String testCase4(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("441=one,X=Y, 442=2,500=three");
		return kv.display();
	}
	
	private static String testCase5(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("18=zzz,441=one,500=three,442=2,442= A,441 =3,35=D,500=ok  ");
		return kv.display();
	}
	
	private static String testCase6(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("441=3,500=not ok,13=qwerty");
		return kv.display();
	}
	
	private static String testCase7(final MyErrorListener errorListener) {
		final MyKeysAndValuesImplementation kv = new MyKeysAndValuesImplementation(errorListener);
		kv.accept("500= three , 6 = 7 ,441= one,442=1,442=4");
		return kv.display();
	}
}
