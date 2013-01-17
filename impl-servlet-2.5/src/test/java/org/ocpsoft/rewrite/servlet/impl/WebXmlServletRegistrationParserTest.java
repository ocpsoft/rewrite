package org.ocpsoft.rewrite.servlet.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.ocpsoft.rewrite.servlet.ServletRegistration;

public class WebXmlServletRegistrationParserTest
{

   @Test(expected = IOException.class)
   public void testShouldThrowIOExceptionForNonXmlInput() throws IOException
   {

      final String webXmlContent = "something_that_is_not_XML";

      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

   }

   @Test(expected = IOException.class)
   public void testShouldThrowIOExceptionForBrokenXml() throws IOException
   {

      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app>some stuff and a missing end tag\n";

      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

   }

   @Test
   public void testShouldParseValidXmlDocumentsWithoutServlets() throws IOException
   {

      // GIVEN an XML file that is not a web.xml
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<start>That is not a web.xml</start>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return no registrations
      assertNotNull(parser.getRegistrations());
      assertEquals(0, parser.getRegistrations().size());

   }

   @Test
   public void testShouldParseEmptyWebXmlWithoutServlets() throws IOException
   {

      // GIVEN an empty web.xml
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n" +
               "\n" +
               "</web-app>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return no registrations
      assertNotNull(parser.getRegistrations());
      assertEquals(0, parser.getRegistrations().size());

   }

   @Test
   public void testShouldParseWebXmlWithServletWithoutMapping() throws IOException
   {

      // GIVEN a Servlet without any mappings
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n" +
               "\n" +
               "  <servlet>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>\n" +
               "    <load-on-startup>1</load-on-startup>\n" +
               "  </servlet>\n" +
               "\n" +
               "</web-app>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return one registration without any mappings
      assertNotNull(parser.getRegistrations());
      assertEquals(1, parser.getRegistrations().size());
      ServletRegistration registation = parser.getRegistrations().get(0);
      assertEquals("javax.faces.webapp.FacesServlet", registation.getClassName());
      assertEquals(0, registation.getMappings().size());

   }

   @Test
   public void testShouldParseWebXmlWithServletWithMapping() throws IOException
   {

      // GIVEN a Servlet with one mapping
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n" +
               "\n" +
               "  <servlet>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>\n" +
               "    <load-on-startup>1</load-on-startup>\n" +
               "  </servlet>\n" +
               "\n" +
               "  <servlet-mapping>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <url-pattern>/faces/*</url-pattern>\n" +
               "  </servlet-mapping>\n" +
               "\n" +
               "</web-app>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return one registration
      assertNotNull(parser.getRegistrations());
      assertEquals(1, parser.getRegistrations().size());

      // AND there should be one mapping
      ServletRegistration registation = parser.getRegistrations().get(0);
      assertEquals("javax.faces.webapp.FacesServlet", registation.getClassName());
      assertEquals(1, registation.getMappings().size());
      assertEquals("/faces/*", registation.getMappings().get(0));

   }

   @Test
   public void testShouldParseWebXmlWithMultipleServletMappings() throws IOException
   {

      // GIVEN a Servlet with two mappings
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n" +
               "\n" +
               "  <servlet>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>\n" +
               "    <load-on-startup>1</load-on-startup>\n" +
               "  </servlet>\n" +
               "\n" +
               "  <servlet-mapping>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <url-pattern>/faces/*</url-pattern>\n" +
               "  </servlet-mapping>\n" +
               "\n" +
               "  <servlet-mapping>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <url-pattern>*.jsf</url-pattern>\n" +
               "  </servlet-mapping>\n" +
               "\n" +
               "</web-app>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return one registration
      assertNotNull(parser.getRegistrations());
      assertEquals(1, parser.getRegistrations().size());

      // AND there should be both mappings in the registation
      ServletRegistration registation = parser.getRegistrations().get(0);
      assertEquals("javax.faces.webapp.FacesServlet", registation.getClassName());
      assertEquals(2, registation.getMappings().size());
      assertEquals("/faces/*", registation.getMappings().get(0));
      assertEquals("*.jsf", registation.getMappings().get(1));

   }

   @Test
   public void testShouldParseWebXmlMappingButMissingServlet() throws IOException
   {

      // GIVEN a Servlet with one mapping but without a matching servlet
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n" +
               "\n" +
               "  <servlet-mapping>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <url-pattern>/faces/*</url-pattern>\n" +
               "  </servlet-mapping>\n" +
               "\n" +
               "</web-app>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return no registation
      assertNotNull(parser.getRegistrations());
      assertEquals(0, parser.getRegistrations().size());

   }

   @Test
   public void testShouldParseWebXmlWithMultipleServletsAndMappings() throws IOException
   {

      // GIVEN multiple Servlets with mappings
      final String webXmlContent = "<?xml version=\"1.0\"?>\n" +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" version=\"3.0\">\n" +
               "\n" +
               "  <servlet>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>\n" +
               "    <load-on-startup>1</load-on-startup>\n" +
               "  </servlet>\n" +
               "\n" +
               "  <servlet>\n" +
               "    <servlet-name>Other Servlet</servlet-name>\n" +
               "    <servlet-class>com.example.MyServlet</servlet-class>\n" +
               "  </servlet>\n" +
               "\n" +
               "  <servlet-mapping>\n" +
               "    <servlet-name>Faces Servlet</servlet-name>\n" +
               "    <url-pattern>/faces/*</url-pattern>\n" +
               "  </servlet-mapping>\n" +
               "\n" +
               "  <servlet-mapping>\n" +
               "    <servlet-name>Other Servlet</servlet-name>\n" +
               "    <url-pattern>/*</url-pattern>\n" +
               "  </servlet-mapping>\n" +
               "\n" +
               "</web-app>\n";

      // WHEN the parser processes the input
      WebXmlServletRegistrationParser parser = new WebXmlServletRegistrationParser();
      parser.parse(new ByteArrayInputStream(webXmlContent.getBytes(Charset.forName("UTF8"))));

      // THEN it should return two registration
      assertNotNull(parser.getRegistrations());
      assertEquals(2, parser.getRegistrations().size());

      // AND the first one should be the first Servlet occuring
      ServletRegistration registation1 = parser.getRegistrations().get(0);
      assertEquals("javax.faces.webapp.FacesServlet", registation1.getClassName());
      assertEquals(1, registation1.getMappings().size());
      assertEquals("/faces/*", registation1.getMappings().get(0));

      // AND the second one should be the second Servlet occuring
      ServletRegistration registation2 = parser.getRegistrations().get(1);
      assertEquals("com.example.MyServlet", registation2.getClassName());
      assertEquals(1, registation2.getMappings().size());
      assertEquals("/*", registation2.getMappings().get(0));

   }

}
