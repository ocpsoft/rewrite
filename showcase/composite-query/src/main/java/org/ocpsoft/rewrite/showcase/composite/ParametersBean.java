package org.ocpsoft.rewrite.showcase.composite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Strings;

@Named
public class ParametersBean
{
   public List<String> getList()
   {
      List<String> result = new ArrayList<String>();
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
               .getRequest();
      Map<String, String[]> parameterMap = request.getParameterMap();
      if (!parameterMap.isEmpty())
      {
         for (Entry<String, String[]> param : parameterMap.entrySet()) {
            String values = Strings.join(Arrays.asList(param.getValue()), ", ");
            result.add(param.getKey() + ": " + values);
         }
      }
      return result;
   }
}
