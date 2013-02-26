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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for asserting on {@link HttpServletRequest#getHeader(String)} values.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Header extends HttpCondition implements Parameterized
{
   private final ParameterizedPatternParser name;
   private final ParameterizedPatternParser value;

   private Header(final String name, final String value)
   {
      Assert.notNull(name, "Header name pattern cannot be null.");
      Assert.notNull(value, "Header value pattern cannot be null.");
      this.name = new RegexParameterizedPatternParser(name);
      this.value = new RegexParameterizedPatternParser(value);
   }

   /**
    * Return a {@link Header} condition that matches against both header name and values.
    * <p>
    * See also: {@link HttpServletRequest#getHeader(String)}
    * 
    * @param name Regular expression matching the header name
    * @param value Regular expression matching the header value
    */
   public static Header matches(final String name, final String value)
   {
      return new Header(name, value);
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with a name matching the
    * given pattern. The header value is ignored.
    * <p>
    * See also: {@link HttpServletRequest#getHeader(String)}
    * 
    * @param name Regular expression matching the header name
    */
   public static Header exists(final String name)
   {
      return new Header(name, "{" + Header.class.getName() + "_value}");
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with value matching the
    * given pattern. The header name is ignored.
    * 
    * @param value Regular expression matching the header value
    */
   public static Header valueExists(final String value)
   {
      return new Header("{" + Header.class.getName() + "_name}", value);
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      HttpServletRequest request = event.getRequest();
      Enumeration<String> headerNames = request.getHeaderNames();
      if (headerNames != null) {
         for (String header : Collections.list(headerNames))
         {
            if (name.matches(event, context, header) && matchesValue(event, context, request, header))
            {
               return true;
            }
         }
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   private boolean matchesValue(Rewrite event, EvaluationContext context, final HttpServletRequest request,
            final String header)
   {
      Enumeration<String> headers = request.getHeaders(header);
      if (headers != null) {
         for (String contents : Collections.list(headers))
         {
            if (value.matches(event, context, contents))
            {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      Set<String> result = new HashSet<String>();
      result.addAll(name.getRequiredParameterNames());
      result.addAll(value.getRequiredParameterNames());
      return result;
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      name.setParameterStore(store);
      value.setParameterStore(store);
   }
}
