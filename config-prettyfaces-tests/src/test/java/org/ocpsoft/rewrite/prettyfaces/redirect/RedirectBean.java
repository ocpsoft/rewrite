package org.ocpsoft.rewrite.prettyfaces.redirect;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class RedirectBean
{
   public static final String PATH_VALUE = " ? ";
   public static final String QUERY_VALUE = "ora. es";

   private String value;
   private String queryValue;

   public String redirect()
   {
      value = PATH_VALUE;
      queryValue = QUERY_VALUE;
      return "pretty:valued";
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(final String value)
   {
      this.value = value;
   }

   public String getQueryValue()
   {
      return queryValue;
   }

   public void setQueryValue(final String queryValue)
   {
      this.queryValue = queryValue;
   }
}
