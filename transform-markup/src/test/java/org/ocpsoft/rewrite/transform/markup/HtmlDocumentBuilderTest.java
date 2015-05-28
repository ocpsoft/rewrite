package org.ocpsoft.rewrite.transform.markup;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import org.junit.Assert;

import org.junit.Test;
import org.ocpsoft.rewrite.transform.markup.HtmlDocumentBuilder;

public class HtmlDocumentBuilderTest
{

   @Test
   public void shouldCreateHtml5Doctype()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result, startsWith("<!DOCTYPE html>"));
   }

   @Test
   public void shouldCreateHeadElement()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result, containsString("<head>"));
      assertThat(result, containsString("</head>"));
   }

   @Test
   public void shouldCreateBody()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result, containsString("<body>"));
      assertThat(result, containsString("</body>"));
   }

   @Test
   public void shouldAddNoTitleByDefault()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result, not(containsString("<title>")));
      assertThat(result, not(containsString("</title>")));
   }

   @Test
   public void shouldAddTitleIfTitleIsSet()
   {
      String result = new HtmlDocumentBuilder().withTitle("foo").build(null);
      assertThat(result, containsString("<title>foo</title>"));
   }

   @Test
   public void shouldAddNoStylesheetByDefault()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result, not(containsString("<link")));
   }

   @Test
   public void shouldAddNoHeaderInjectionByDefault()
   {
      String result = new HtmlDocumentBuilder().build(null);
      Assert.assertEquals("<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "</head>\n" +
               "<body>\n" +
               "</body>\n" +
               "</html>\n", result);
   }

   @Test
   public void shouldAddStylesheetIfFileWasAdded()
   {
      String result = new HtmlDocumentBuilder().addStylesheet("styles.css").build(null);
      assertThat(result, containsString("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">"));
   }

   @Test
   public void shouldAddMultipleStylesheets()
   {
      String result = new HtmlDocumentBuilder()
               .addStylesheet("foo.css")
               .addStylesheet("bar.css")
               .build(null);
      assertThat(result, containsString("<link rel=\"stylesheet\" type=\"text/css\" href=\"foo.css\">"));
      assertThat(result, containsString("<link rel=\"stylesheet\" type=\"text/css\" href=\"bar.css\">"));
   }

   @Test
   public void shouldAddBody()
   {
      String result = new HtmlDocumentBuilder().build("content");
      assertThat(result, containsString("<body>\ncontent\n</body>"));
   }

}
