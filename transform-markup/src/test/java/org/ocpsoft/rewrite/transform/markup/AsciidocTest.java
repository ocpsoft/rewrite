package org.ocpsoft.rewrite.transform.markup;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class AsciidocTest
{

   @Test
   public void shouldRenderBoldText()
   {

      String asciidoc = "This is **bold**!";
      String html = Asciidoc.partialDocument().transform(asciidoc);

      assertThat(html).contains("This is <strong>bold</strong>!");

   }

   @Test
   public void shouldRenderLiteral()
   {

      String asciidoc = "The +EventMetadata+ interface";
      String html = Asciidoc.partialDocument().transform(asciidoc);

      assertThat(html).contains("The <tt>EventMetadata</tt> interface");

   }

   @Test
   public void shouldRenderHeader()
   {

      String asciidoc = "" +
               "== Some header\n\n" +
               "Some text";
      String html = Asciidoc.partialDocument().transform(asciidoc);

      assertThat(html).matches("(?s).*<h2[^>]*>Some header</h2>.*");

   }

   @Test
   public void shouldRenderSourceCode()
   {

      String asciidoc = "\n" +
               "[source, java] \n" +
               "---- \n" +
               "public interface EventMetadata {    \n" +
               "    public Set<Annotation> getQualifiers();\n" +
               "    public InjectionPoint getInjectionPoint();\n" +
               "    public Type getResolvedType();\n" +
               " } \n" +
               "----";

      String html = Asciidoc.partialDocument().transform(asciidoc);

      assertThat(html).contains("<div class=\"listingblock\">\n" +
               "  \n" +
               "  <div class=\"content monospaced\">\n" +
               "    \n" +
               "    <pre class=\"highlight\"><code class=\"java\">public interface EventMetadata {\n" +
               "    public Set&lt;Annotation&gt; getQualifiers();\n" +
               "    public InjectionPoint getInjectionPoint();\n" +
               "    public Type getResolvedType();\n" +
               " }</code></pre>\n" +
               "    \n" +
               "  </div>\n" +
               "</div>\n" +
               "\n");
   }

}
