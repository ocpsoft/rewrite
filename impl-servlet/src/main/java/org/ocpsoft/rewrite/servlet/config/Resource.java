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
import java.util.Map;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.bind.ParameterizedPattern;
import org.ocpsoft.rewrite.bind.RegexCapture;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.servlet.util.ParameterStore;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} responsible for determining existence of resources within the web root
 * of the servlet container.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Resource extends HttpCondition implements IResource
{
   private static final Logger log = Logger.getLogger(Resource.class);

   private final ParameterizedPattern resource;
   private final ParameterStore<ResourceParameter> parameters = new ParameterStore<ResourceParameter>();

   private Resource(final String resource)
   {
      this.resource = new ParameterizedPattern(resource);

      for (RegexCapture parameter : this.resource.getParameters().values()) {
         where(parameter.getName()).bindsTo(Evaluation.property(parameter.getName()));
      }
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (resource != null)
      {
         String file = resource.build(event, context, parameters.getParameters());
         try {
            if (event.getRequest().getServletContext().getResource(file) != null)
            {
               Map<RegexCapture, String[]> parameters = resource.parse(event, context, file);
               for (RegexCapture capture : parameters.keySet()) {
                  if (!Bindings.enqueueSubmission(event, context, where(capture.getName()), parameters.get(capture)))
                     return false;
               }
               return true;
            }
         }
         catch (MalformedURLException e) {
            log.debug("Invalid file format [{}]", file);
         }
      }
      return false;
   }

   /**
    * Create a new {@link org.ocpsoft.rewrite.config.Condition} that returns true if the given resource exists relative
    * to the web root of the current application.
    */
   public static Resource exists(final String resource)
   {
      return new Resource(resource);
   }

   @Override
   public ResourceParameter where(String param)
   {
      return parameters.where(param, new ResourceParameter(this, resource.getParameter(param)));
   }

   @Override
   public ResourceParameter where(String param, Binding binding)
   {
      return where(param).bindsTo(binding);
   }

   @Override
   public ParameterizedPattern getResourceExpression()
   {
      return resource;
   }

}
