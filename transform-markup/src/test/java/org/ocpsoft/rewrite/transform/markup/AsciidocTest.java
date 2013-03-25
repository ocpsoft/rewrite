package org.ocpsoft.rewrite.transform.markup;

import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

public class AsciidocTest
{

   @Test
   public void shouldRenderBoldText()
   {

      String asciidoc = "This is **bold**!";
      String html = Asciidoc.partialDocument().transform(asciidoc);

      assertThat(html, Matchers.containsString("This is <strong>bold</strong>!"));

   }

   @Test
   public void shouldRenderCode()
   {

      String asciidoc = "The +EventMetadata+ interface";
      String html = Asciidoc.partialDocument().transform(asciidoc);

      assertThat(html, Matchers.containsString("The <tt>EventMetadata</tt> interface"));

   }

}
