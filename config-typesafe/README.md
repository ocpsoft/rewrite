Type-safe method invocation for Rewrite
===============================================

A simple configuration that enables type-safe method invocation in Rewrite

How to use
==========

 * Include OCPSoft Rewrite in your application's POM file:

        <dependency>
           <groupId>org.ocpsoft.rewrite</groupId>
           <artifactId>rewrite-servlet</artifactId>
           <version>${rewrite.version}</version>
        </dependency>

 * Also include the type-safe config extension:

        <dependency>
           <groupId>org.ocpsoft.rewrite</groupId>
           <artifactId>rewrite-config-typesafe</artifactId>
           <version>${rewrite.version}</version>
        </dependency>
        
 * Create rules using type-safe method invocation!
 
        Typesafe typesafeInvocation = Typesafe.method();
        typesafe.invoke(MyObject.class).myMethod(typesafe.param(long.class, "id"));
 
        ConfigurationBuilder.begin()
           .defineRule()
           .when(Path.matches("/id/{id}")
               .where("id")
               .convertedBy(IntegerConverter.class))
               .validatedBy(IntegerValidator.class))
           .perform(typesafeInvocation);