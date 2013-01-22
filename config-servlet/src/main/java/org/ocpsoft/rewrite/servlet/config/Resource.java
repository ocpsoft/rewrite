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
import org.ocpsoft.rewrite.bind.Bindings;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParameter;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * A {@link org.ocpsoft.rewrite.config.Condition} responsible for determining existence of resources within the web root
 * of the servlet container.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Resource extends HttpCondition implements Parameterized<ParameterizedPatternParameter, String>
{
   private static final Logger log = Logger.getLogger(Resource.class);

   private final ParameterizedPatternParser resource;

   private Resource(final String resource)
   {
      this.resource = new RegexParameterizedPatternParser(resource);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (resource != null)
      {
         String file = resource.getBuilder().build(event, context);
         try {
            if (event.getServletContext().getResource(file) != null)
            {
               Map<ParameterizedPatternParameter, String[]> parameters = resource.parse(event, context, file);
               for (ParameterizedPatternParameter capture : parameters.keySet()) {
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

   public ParameterizedPatternParser getResourceExpression()
   {
      return resource;
   }

   @Override
   public ParameterStore<ParameterizedPatternParameter> getParameterStore()
   {
      return resource.getParameterStore();
   }

}
