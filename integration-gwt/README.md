Rewrite GWT Integration
=================================================

This enables the HTML5 History.pushState(...) support in your application, which will now use pretty/URLs whenever possible instead of the '#' anchor tag with 'name=value' pairs. Note that you must modify your place tokenization to process the new information.

Get Started
===========

1. Include Rewrite GWT in your application's POM file:

        <dependency>
            <groupId>org.ocpsoft.rewrite</groupId>
            <artifactId>rewrite-integration-gwt</artifactId>
            <version>${rewrite.version}</version>
        </dependency>

2. Include Rewrite GWT in your App.gwt.xml file:

        <inherits name="org.ocpsoft.rewrite.gwt.Rewrite" />

3. Enable bookmarking by routing some or all URLs back to your host application file. Below is a recommended rule format (replace */index.jsp* with the path to your application):

        public class HistoryRewriteConfiguration extends HttpConfigurationProvider
        {
           public static Logger log = Logger.getLogger(HistoryRewriteConfiguration.class);
        
           @Override
           public Configuration getConfiguration(ServletContext context)
           {
              return ConfigurationBuilder
                   .begin()
               
                   .addRule(Join.path("/").to("/index.jsp").withInboundCorrection())
            
                   .defineRule()
                   .when(DispatchType.isRequest()
                        .and(Path.matches("{path}").where("path").matches(".*"))
                        .andNot(Resource.exists("{path}"))
                        .andNot(ServletMapping.includes("{path}")))
                   .perform(Forward.to("/index.jsp"));
           }
            
           @Override
           public int priority()
           {
              return 0;
           }
        }

4. Run your application!

FEEDBACK
========
This project is looking for your feedback! What would make your task easier? How can we simplify this experience?

TODO's
======

 * Implement GWT 'name=value' pair tokenization mapping support.

