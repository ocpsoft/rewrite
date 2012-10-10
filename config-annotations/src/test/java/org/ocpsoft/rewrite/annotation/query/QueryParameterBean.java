package org.ocpsoft.rewrite.annotation.query;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

@Named
@RequestScoped
@Join(path = "/query", to = "/query.jsp")
public class QueryParameterBean
{

   @Parameter("q")
   private String value;

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

}
