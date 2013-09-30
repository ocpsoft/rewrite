package org.ocpsoft.rewrite.prettyfaces.encoding;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class EncodingBean
{
   private String pathText;
   private String queryText;
   private String param0;
   private String param1;

   public String action()
   {
      return "";
   }

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

   public String getParam0()
   {
      return param0;
   }

   public void setParam0(String param0)
   {
      this.param0 = param0;
   }

   public String getParam1()
   {
      return param1;
   }

   public void setParam1(String param1)
   {
      this.param1 = param1;
   }
}
