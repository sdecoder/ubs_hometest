package org.hometest.ubs.ubs_hometest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    	MyKeysAndValuesImplementation errorListener = (MyKeysAndValuesImplementation) context.getBean(MyKeysAndValuesImplementation.class);
    	if (errorListener != null) {
    		System.err.println("[dbg] errorListener has been created successfully");
    	}
    }
}
