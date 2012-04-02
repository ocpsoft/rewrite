Rewrite CDI Integration
=================================================

This enables CDI support in your service implementations, such as configuration providers, listeners, and more.

Get Started
==========

1. Include OCPSoft Rewrite in your application's POM file:

        <dependency>
           <groupId>org.ocpsoft.rewrite</groupId>
           <artifactId>rewrite-integration-cdi</artifactId>
           <version>${rewrite.version}</version>
        </dependency>

2. Use CDI in your configuration providers or listeners:

	    package com.example;
    
	    public class ExampleConfigurationProvider extends HttpConfigurationProvider
	    {
             @Inject @My
			    private RuleDatabase source;
    
   		    @Override
   		    public int priority()
   		    {
      			    return 10;
   		    }
    
   		    @Override
   		    public Configuration getConfiguration(final ServletContext context)
   		    {
     	 		    return ConfigurationBuilder.begin()
               		    .addRule()
               		    .setCondition(new DatabaseConditionAdapter(source)) 
	               	    .setOperation(new DatabaseOperationAdapter(source));
		    }
	    }
    
3. Run your application!

FEEDBACK
========

This project is looking for your feedback! What would make your task easier? How can we simplify this experience?

TODO's
======

 * Implement CDI value injection support
