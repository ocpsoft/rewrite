package com.ocpsoft.rewrite.gwt.server.history;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map.Entry;

import javax.servlet.ServletRegistration;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.bind.Evaluation;
import com.ocpsoft.rewrite.bind.ParameterizedPattern;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.servlet.config.HttpCondition;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

public class ServletMapping extends HttpCondition
{
   // TODO move to rewrite proper
   private static final Logger log = Logger.getLogger(Resource.class);

   private ParameterizedPattern resource;

   private ServletMapping(final String resource)
   {
      this.resource = new ParameterizedPattern(resource);

      for (Parameter<String> parameter : this.resource.getParameters().values()) {
         parameter.bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (resource != null)
      {
         String path = resource.build(event, context);
         try {
            
            for(Entry<String, ? extends ServletRegistration> entry : event.getRequest().getServletContext().getServletRegistrations().entrySet())
            {
               ServletRegistration servlet = entry.getValue();
               Collection<String> mappings = servlet.getMappings();
               
               for (String mapping : mappings) {
                  if(path.startsWith("/") && !mapping.startsWith("/"))
                  {
                     mapping = "/" + mapping;
                  }
                  
                  if(mapping.contains("*"))
                  {
                     mapping = mapping.replaceAll("\\*", ".*");
                  }
                  
                  if(path.matches(mapping))
                  {
                     return true;
                  }
               }
            }
            
            return event.getRequest().getServletContext().getResource(path) != null;
         }
         catch (MalformedURLException e) {
            log.debug("Invalid file format [{}]", path);
         }
      }
      return false;
   }

   public static ServletMapping includes(final String resource)
   {
      return new ServletMapping(resource);
   }
}