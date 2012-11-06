/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.servlet.config;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletRegistration;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.RegexParameterizedPattern;
import org.ocpsoft.rewrite.param.PatternParameter;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} responsible for comparing URLs to Servlet Mappings.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServletMapping extends HttpCondition implements IServletMapping
{
   private static final Logger log = Logger.getLogger(Resource.class);

   private final RegexParameterizedPattern resource;
   private final ParameterStore<ServletMappingParameter> parameters = new ParameterStore<ServletMappingParameter>();

   private ServletMapping(final String resource)
   {
      this.resource = new RegexParameterizedPattern(resource);

      for (PatternParameter parameter : this.resource.getParameterMap().values()) {
         where(parameter.getName()).bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (resource != null)
      {
         String path = resource.build(event, context, parameters);
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

   @Override
   public ServletMappingParameter where(String param)
   {
      return parameters.where(param, new ServletMappingParameter(this, resource.getParameter(param)));
   }

   @Override
   public ServletMappingParameter where(String param, Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public RegexParameterizedPattern getResourceExpression()
   {
      return resource;
   }
}