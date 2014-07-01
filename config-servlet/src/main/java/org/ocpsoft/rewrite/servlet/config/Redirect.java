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

import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Transpositions;

/**
 * An {@link Operation} that performs redirects via {@link HttpInboundServletRewrite#redirectPermanent(String)} and
 * {@link HttpInboundServletRewrite#redirectTemporary(String)}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Redirect extends HttpOperation implements Parameterized
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
         String target = location.build(event, context, Transpositions.encodePath());
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
    * Create an {@link Operation} that issues a permanent {@link Redirect} ( 301
    * {@link HttpServletResponse#SC_MOVED_PERMANENTLY} ) to the given location. If the given location is not the same as
    * {@link HttpServletRewrite#getAddress()}, this will change the browser {@link URL} and result in a new request.
    * 
    * <p>
    * Note that in order to redirect to a resource within the {@link ServletContext}, you must prepend the
    * {@link ServletContext#getContextPath()}.
    * 
    * <p>
    * For example:<br/>
    * <code>
    *    Redirect.permanent(contextPath + "/example/location.html") <br>
    * </code>
    * 
    * <p>
    * The given location may be parameterized:
    * <p>
    * <code>
    *    /store/global
    *    /store/{category} <br>
    *    /store/{category}/item/{itemId} <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the target location.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static Redirect permanent(final String location)
   {
      return new Redirect(location, RedirectType.PERMANENT) {
         @Override
         public String toString()
         {
            return "Redirect.permanent(\"" + location + "\")";
         }
      };
   }

   /**
    * Create an {@link Operation} that issues a temporary {@link Redirect} ( 302
    * {@link HttpServletResponse#SC_MOVED_TEMPORARILY} ) to the given location. If the given location is not the same as
    * {@link HttpServletRewrite#getAddress()}, this will change the browser {@link URL} and result in a new request.
    * <p>
    * Note that in order to redirect within the {@link ServletContext}, you must prepend the
    * {@link ServletContext#getContextPath()}.
    * <p>
    * For example:<br/>
    * <code>
    *    Redirect.temporary(contextPath + "/example/location.html") <br>
    * </code>
    * 
    * <p>
    * The given location may be parameterized:
    * <p>
    * <code>
    *    /store/global
    *    /store/{category} <br>
    *    /store/{category}/item/{itemId} <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the target location.
    * 
    * @see ConfigurationRuleParameterBuilder#where(String)
    */
   public static Redirect temporary(final String location)
   {
      return new Redirect(location, RedirectType.TEMPORARY) {
         @Override
         public String toString()
         {
            return "Redirect.temporary(\"" + location + "\")";
         }
      };
   }

   private enum RedirectType
   {
      PERMANENT(301),
      TEMPORARY(302);

      private int code;

      private RedirectType(int code)
      {
         this.code = code;
      }

      @SuppressWarnings("unused")
      public int getCode()
      {
         return code;
      }
   }

   public RegexParameterizedPatternBuilder getTargetExpression()
   {
      return location;
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
