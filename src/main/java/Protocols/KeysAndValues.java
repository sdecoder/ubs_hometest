package Protocols;
/*### void accept(String s)

 * kvPairs = zero, one or more comma separated, key-value pairs (e.g. "pi=314159,hello=world")
 * Trim all leading & trailing whitespace
 * numeric integer values accumulate
 * non-integers overwrite
 * problematic values are reported via the ErrorListener interface (see below)

#### Examples
```text
14=15
A=B52
dry=D.R.Y.
14=7
14=4
dry=Don't Repeat Yourself
```

After invoking `accept()` on each of these key-value strings in the above order, 

 * key **14** is equal to _26_ (i.e. _15 + 7 + 4_  ...an integer) 
 * key **dry** is equal to _Don't repeat yourself_  (...a string)

invoking `accept("14=15, 14=7,A=B52, 14 = 4, dry = Don't Repeat Yourself")` has the same effect.

### String display()

 * String displays all key-value pairs (one pair per line)
 * Keys are sorted (alpha-ascending, case-insensitive)

#### Example

Assuming... 
```java
KeysAndValues kv = new MyKeysAndValuesImplementation(listener);
kv.accept("one=two");
kv.accept("Three=four");
kv.accept("5=6");
kv.accept("14=X");
String displayText = kv.display();
```

Then `displayText` will equal...

```text
14=X
5=6
one=two
Three=four
```*/
public interface KeysAndValues {
	 void accept(String kvPairs);
   String display();
}
