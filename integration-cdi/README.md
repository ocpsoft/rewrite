Rewrite CDI Integration
=================================================

Get Started
==========

1. Include OCPSoft Rewrite in your application's POM file:

        <dependency>
           <groupId>com.ocpsoft.rewrite</groupId>
           <artifactId>rewrite-integation-cdi</artifactId>
           <version>${rewrite.version}</version>
        </dependency>

2. This enables CDI support in your service implementations, such as configuration providers, listeners, and more.

3. Use CDI in your configuration providers or listeners:

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

4. Run your application!

FEEDBACK
========

This project is looking for your feedback! What would make your task easier? How can we simplify this experience?

TODO's
======

 * Implement CDI value injection support
