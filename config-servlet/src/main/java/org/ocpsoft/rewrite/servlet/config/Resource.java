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
import java.util.Set;

import javax.servlet.ServletContext;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Transpositions;
import org.ocpsoft.urlbuilder.Address;

/**
 * A {@link Condition} responsible for determining existence of resources within the
 * {@link ServletContext#getResourcePaths(String)} of the servlet container.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Resource extends HttpCondition implements Parameterized
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
         ParameterizedPatternBuilder builder = resource.getBuilder();
         if (builder.isParameterComplete(event, context))
         {
            String file = builder.build(event, context, Transpositions.encodePath());
            try
            {
               return (event.getServletContext().getResource(file) != null);
            }
            catch (MalformedURLException e)
            {
               log.debug("Invalid file format [{}]", file);
            }
         }
         else
         {
            /*
             * Parameter didn't exist, that's OK, switch to parsing mode.
             */
            @SuppressWarnings("unchecked")
            Set<String> paths = event.getServletContext().getResourcePaths("/");
            for (String path : paths)
            {
               if (resource.parse(path).matches())
               {
                  return true;
               }
            }
         }
      }
      return false;
   }

   /**
    * Create a {@link Condition} that returns <code>true</code> if the given resource exists in the
    * {@link ServletContext#getResourcePaths(String)} of the current application.
    * 
    * <p>
    * The given resource path may be parameterized:
    * <p>
    * <code>
    *    /example/{param}.html <br>
    *    /css/{value}.css <br>
    *    ... 
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the {@link Address} of the internal resource.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static Resource exists(final String resource)
   {
      return new Resource(resource) {
         @Override
         public String toString()
         {
            return "Resource.exists(\"" + resource + "\")";
         }
      };
   }

   /**
    * Get the {@link ParameterizedPattern} of this {@link Resource}.
    */
   public ParameterizedPatternParser getExpression()
   {
      return resource;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return resource.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      resource.setParameterStore(store);
   }

}
