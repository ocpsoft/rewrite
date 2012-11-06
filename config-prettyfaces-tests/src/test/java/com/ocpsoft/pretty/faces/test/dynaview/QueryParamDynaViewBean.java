package com.ocpsoft.pretty.faces.test.dynaview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;
import com.ocpsoft.pretty.faces.annotation.URLValidator;

@ManagedBean
@RequestScoped
@URLMapping(id = "queryParamMapping", pattern = "/queryparam", 
      viewId = "#{queryParamDynaViewBean.computeQueryParamViewId}")
public class QueryParamDynaViewBean
{

   /**
    * Injected value of the path parameter
    */
   @URLQueryParameter(value="param", mappingId="queryParamMapping")
   @URLValidator(validatorIds="DynaViewParameterValidator")
   private String queryParamParam;
   
   /**
    * This method is used to compute the view id! It will return
    * <code>/correct.jsf</code> if the query parameter property contains the
    * string <code>correct</code>. In all other cases it will return
    * <code>/wrong.jsf</code>.
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
