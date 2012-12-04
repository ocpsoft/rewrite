package org.ocpsoft.rewrite.prettyfaces.dynaview;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLValidator;

@RequestScoped
@Named("pathParamDynaViewBean")
@URLMapping(id = "pathParamMapping", pattern = "/pathparam/#{pathParamDynaViewBean.pathParam}",
         viewId = "#{pathParamDynaViewBean.computePathParamViewId}",
         validation = @URLValidator(index = 0, validatorIds = "ParameterizedDynaViewValidator"))
public class ParameterizedDynaViewPathParamBean
{

   /**
    * Injected value of the path parameter
    */
   private String pathParam;

   /**
    * This method is used to compute the view id! It will return <code>/correct.jsf</code> if the path parameter
    * property contains the string <code>correct</code>. In all other cases it will return <code>/wrong.jsf</code>.
    */
   public String computePathParamViewId()
   {
      if (pathParam != null && pathParam.equals("correct"))
      {
         return "/correct.jsf";
      }
      else
      {
         return "/wrong.jsf";
      }

   }

   public String getPathParam()
   {
      return pathParam;
   }

   public void setPathParam(String value)
   {
      this.pathParam = value;
   }

}
