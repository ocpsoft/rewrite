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

import java.net.URL;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Transformations;

/**
 * An {@link Operation} that performs redirects via {@link HttpInboundServletRewrite#redirectPermanent(String)} and
 * {@link org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite#redirectTemporary(String)}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Redirect extends HttpOperation implements Parameterized
{
   private final RedirectType type;

   private final RegexParameterizedPatternBuilder location;

   private Redirect(final String location, final RedirectType type)
   {
      this.location = new RegexParameterizedPatternBuilder("[^/]+", location);
      this.type = type;
   }

   @Override
   public void performHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      if (event instanceof HttpInboundServletRewrite)
      {
         String target = location.build(event, context, Transformations.encodePath());
         switch (type)
         {
         case PERMANENT:
            ((HttpInboundServletRewrite) event).redirectPermanent(target);
            break;
         case TEMPORARY:
            ((HttpInboundServletRewrite) event).redirectTemporary(target);
            break;
         default:
            break;
         }
      }
   }

   /**
    * Issue a permanent redirect ( 301 {@link HttpServletResponse#SC_MOVED_PERMANENTLY} ) to the given location. If the
    * given location is not the same as {@link HttpServletRewrite#getAddress()}, this will change the browser
    * {@link URL} and result in a new request. Note that in order to redirect within the {@link ServletContext}, you
    * must prepend the {@link ServletContext#getContextPath()} manually.
    * <p>
    * The given location may be parameterized using the following format:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{value}/sub/{value2} <br>
    *    ... and so on
    * </code>
    * <p>
    * By default, matching parameter values are bound to the {@link org.ocpsoft.rewrite.context.EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
    */
   public static Redirect permanent(final String location)
   {
      return new Redirect(location, RedirectType.PERMANENT);
   }

   /**
    * Issue a temporary redirect ( 302 {@link HttpServletResponse#SC_MOVED_TEMPORARILY} ) to the given location. If the
    * given location is not the same as {@link HttpServletRewrite#getAddress()}, this will change the browser
    * {@link URL} and result in a new request. Note that in order to redirect within the {@link ServletContext}, you
    * must prepend the {@link ServletContext#getContextPath()} manually.
    * <p>
    * The given location may be parameterized using the following format:
    * <p>
    * <code>
    *    /example/{param} <br>
    *    /example/{value}/sub/{value2} <br>
    *    ... and so on
    * </code>
    * <p>
    * By default, matching parameter values are bound to the {@link EvaluationContext}.
    * <p>
    * See also {@link #where(String)}
    */
   public static Redirect temporary(final String location)
   {
      return new Redirect(location, RedirectType.TEMPORARY);
   }

   private enum RedirectType
   {
      /**
       * 301
       */
      PERMANENT,
      /**
       * 302
       */
      TEMPORARY
   }

   public RegexParameterizedPatternBuilder getTargetExpression()
   {
      return location;
   }

   @Override
   public String toString()
   {
      return "Redirect [type=" + type + ", location=" + location + "]";
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return location.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      location.setParameterStore(store);
   }
}
