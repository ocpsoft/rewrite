package org.ocpsoft.rewrite.prettyfaces.loop;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

@Named
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
