Tuckey URLRewriteFilter integration for Rewrite
===============================================

A (for now) simple integration that loads the standard WEB-INF/urlrewrite.xml file for use in Rewrite

How to use
==========


 * Remove URLRewriteFilter from your web.xml and POM.

 * Leave your existing /WEB-INF/urlrewrite.xml configuration file in place.

 * Include OCPSoft Rewrite in your application's POM file:

<dependency>
   <groupId>com.ocpsoft.rewrite</groupId>
   <artifactId>rewrite-config-tuckey</artifactId>
   <version>${rewrite.version}</version>
</dependency>

 * Run your Rewrite enhanced application!

TODO's
======

 * Integrate with mod_rewrite style configuration. 

 * Support Tuckey.org init parameters and configuration reloading.