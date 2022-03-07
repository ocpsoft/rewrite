package org.ocpsoft.rewrite.transform.markup;

import org.junit.Test;
import org.ocpsoft.rewrite.transform.markup.HtmlDocumentBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlDocumentBuilderTest
{

   @Test
   public void shouldCreateHtml5Doctype()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result).startsWith("<!DOCTYPE html>");
   }

   @Test
   public void shouldCreateHeadElement()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result).contains("<head>");
      assertThat(result).contains("</head>");
   }

   @Test
   public void shouldCreateBody()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result).contains("<body>");
      assertThat(result).contains("</body>");
   }

   @Test
   public void shouldAddNoTitleByDefault()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result).doesNotContain("<title>");
      assertThat(result).doesNotContain("</title>");
   }

   @Test
   public void shouldAddTitleIfTitleIsSet()
   {
      String result = new HtmlDocumentBuilder().withTitle("foo").build(null);
      assertThat(result).contains("<title>foo</title>");
   }

   @Test
   public void shouldAddNoStylesheetByDefault()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result).doesNotContain("<link");
   }

   @Test
   public void shouldAddNoHeaderInjectionByDefault()
   {
      String result = new HtmlDocumentBuilder().build(null);
      assertThat(result).isEqualTo("<!DOCTYPE html>\n" +
              "<html>\n" +
              "<head>\n" +
              "</head>\n" +
              "<body>\n" +
              "</body>\n" +
              "</html>\n");
   }

   @Test
   public void shouldAddStylesheetIfFileWasAdded()
   {
      String result = new HtmlDocumentBuilder().addStylesheet("styles.css").build(null);
      assertThat(result).contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">");
   }

   @Test
   public void shouldAddMultipleStylesheets()
   {
      String result = new HtmlDocumentBuilder()
               .addStylesheet("foo.css")
               .addStylesheet("bar.css")
               .build(null);
      assertThat(result).contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"foo.css\">");
      assertThat(result).contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"bar.css\">");
   }

   @Test
   public void shouldAddBody()
   {
      String result = new HtmlDocumentBuilder().build("content");
      assertThat(result).contains("<body>\ncontent\n</body>");
   }

}
