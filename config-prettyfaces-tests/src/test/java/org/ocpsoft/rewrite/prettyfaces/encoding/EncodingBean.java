package org.ocpsoft.rewrite.prettyfaces.encoding;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class EncodingBean
{
   private String pathText;
   private String queryText;

   public String getPathText()
   {
      return pathText;
   }

   public void setPathText(final String pathText)
   {
      this.pathText = pathText;
   }

   public String getQueryText()
   {
      return queryText;
   }

   public void setQueryText(final String queryText)
   {
      this.queryText = queryText;
   }
}
