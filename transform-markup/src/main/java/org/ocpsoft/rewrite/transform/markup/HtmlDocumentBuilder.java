package org.ocpsoft.rewrite.transform.markup;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.common.util.Strings;
import org.ocpsoft.rewrite.transform.Transformer;

/**
 * Helper class that can be used by {@link Transformer} implementations to render full HTML documents.
 *
 * @author Christian Kaltepoth
 */
class HtmlDocumentBuilder
{

   private String title;

   private final List<String> stylesheets = new ArrayList<String>();
   private final List<String> headerInjections = new ArrayList<String>();

   public HtmlDocumentBuilder addStylesheet(String url)
   {
      this.stylesheets.add(url);
      return this;
   }

   public HtmlDocumentBuilder withTitle(String title)
   {
      this.title = title;
      return this;
   }

   public HtmlDocumentBuilder addHeaderInjection(String element)
   {
      headerInjections.add(element);
      return this;
   }

   public String build(String body)
   {

      StringBuilder result = new StringBuilder();

      result.append("<!DOCTYPE html>\n");
      result.append("<html>\n");

      result.append("<head>\n");
      if (!Strings.isNullOrEmpty(title)) {
         result.append("<title>").append(title.trim()).append("</title>\n");
      }
      for (String stylesheet : stylesheets) {
         result.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
         result.append(stylesheet);
         result.append("\">\n");
      }
      for (String headerInjection : headerInjections) {
         result.append(headerInjection);
         result.append("\n");
      }
      result.append("</head>\n");

      result.append("<body>\n");
      if (!Strings.isNullOrEmpty(body)) {
         result.append(body).append("\n");
      }
      result.append("</body>\n");

      result.append("</html>\n");
      return result.toString();

   }

}
