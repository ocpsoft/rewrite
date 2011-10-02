<!-- A test JSP page for validating that a custom module is installed. This
tries to load the org.jboss.test.modules.TestClass which is added to the
server by adding an org/jboss/test/modules/main/* with the org.jboss.test.modules.jar
and module.xml descriptor to the application .openshift/config/modules git
repo and pushing it.
-->
<HTML>
<HEAD>
	<TITLE>JBossAS7 Custom Modules Test Page</TITLE>
    <%@ page import="org.jboss.test.modules.TestClass" %>
</HEAD>
<BODY>
<h1>org.jboss.test.modules</h1>
<pre>
<%= TestClass.validate() %>
</pre>
</BODY>
</HTML>
