package org.ocpsoft.rewrite.prettyfaces.dynaview;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;
import com.ocpsoft.pretty.faces.annotation.URLValidator;

@RequestScoped
@Named("queryParamDynaViewBean")
@URLMapping(id = "queryParamMapping", pattern = "/queryparam",
         viewId = "#{queryParamDynaViewBean.computeQueryParamViewId}")
public class ParameterizedDynaViewQueryParamBean
{

   /**
    * Injected value of the path parameter
    */
   @URLQueryParameter(value = "param", mappingId = "queryParamMapping")
   @URLValidator(validatorIds = "ParameterizedDynaViewValidator")
   private String queryParamParam;

   /**
    * This method is used to compute the view id! It will return <code>/correct.jsf</code> if the query parameter
    * property contains the string <code>correct</code>. In all other cases it will return <code>/wrong.jsf</code>.
    */
   public String computeQueryParamViewId()
   {

      if (queryParamParam != null && queryParamParam.equals("correct"))
      {
         return "/correct.jsf";
      }
      else
      {
         return "/wrong.jsf";
      }

   }

   public String getQueryParamParam()
   {
      return queryParamParam;
   }

   public void setQueryParamParam(String queryParamParam)
   {
      this.queryParamParam = queryParamParam;
   }

}
