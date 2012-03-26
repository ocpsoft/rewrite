package com.ocpsoft.rewrite.servlet.config;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

import org.ocpsoft.logging.Logger;

import com.ocpsoft.rewrite.bind.Evaluation;
import com.ocpsoft.rewrite.bind.ParameterizedPattern;
import com.ocpsoft.rewrite.config.Condition;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.param.Parameter;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link Condition} responsible for comparing URLs to Servlet Mappings.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServletMapping extends HttpCondition
{
   private static final Logger log = Logger.getLogger(Resource.class);

   private final ParameterizedPattern resource;

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

            for (Entry<String, ? extends ServletRegistration> entry : event.getRequest().getServletContext()
                     .getServletRegistrations().entrySet())
            {
               ServletRegistration servlet = entry.getValue();
               Collection<String> mappings = servlet.getMappings();

               for (String mapping : mappings) {
                  if (path.startsWith("/") && !mapping.startsWith("/"))
                  {
                     mapping = "/" + mapping;
                  }

                  if (mapping.contains("*"))
                  {
                     mapping = mapping.replaceAll("\\*", ".*");
                  }

                  if (path.matches(mapping))
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

   /**
    * Create a condition which returns true if the given resource is mapped by any {@link Servlet} instances registered
    * within the current application, and returns false if no {@link Servlet} will handle the resource.
    */
   public static ServletMapping includes(final String resource)
   {
      return new ServletMapping(resource);
   }
}