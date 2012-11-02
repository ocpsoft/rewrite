package com.ocpsoft.pretty.faces.test.encoding;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
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
