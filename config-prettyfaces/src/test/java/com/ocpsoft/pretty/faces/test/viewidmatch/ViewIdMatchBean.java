package com.ocpsoft.pretty.faces.test.viewidmatch;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@RequestScoped
// Please note that this pattern also matches the viewId
@URLMapping(pattern = "/path/#{viewIdMatchBean.value}", viewId = "/path/viewidmatch.jsf")
public class ViewIdMatchBean
{

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
