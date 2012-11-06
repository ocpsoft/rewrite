package com.ocpsoft.pretty.faces.test.dynaview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLValidator;

@ManagedBean
@RequestScoped
@URLMapping(id = "pathParamMapping", pattern = "/pathparam/#{pathParamDynaViewBean.pathParam}", 
      viewId = "#{pathParamDynaViewBean.computePathParamViewId}",
      validation=@URLValidator(index=0, validatorIds="DynaViewParameterValidator"))
public class PathParamDynaViewBean
{

   /**
    * Injected value of the path parameter
    */
   private String pathParam;

   /**
    * This method is used to compute the view id! It will return
    * <code>/correct.jsf</code> if the path parameter property contains the
    * string <code>correct</code>. In all other cases it will return
    * <code>/wrong.jsf</code>.
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
