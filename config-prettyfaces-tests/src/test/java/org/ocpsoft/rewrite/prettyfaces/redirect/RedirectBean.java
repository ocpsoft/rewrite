package org.ocpsoft.rewrite.prettyfaces.redirect;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class RedirectBean
{
   public static final String PATH_VALUE = " ? ";
   public static final String QUERY_VALUE = "ora. es";

   public String value;
   public String queryValue;

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
