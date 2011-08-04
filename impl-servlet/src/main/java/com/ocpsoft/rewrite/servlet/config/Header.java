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
package com.ocpsoft.rewrite.servlet.config;

import java.util.Collections;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.ocpsoft.rewrite.EvaluationContext;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Header extends HttpCondition
{
   private final Pattern name;
   private final Pattern value;

   private Header(final String nameRegex, final String valueRegex)
   {
      Assert.notNull(nameRegex, "Header name pattern cannot be null.");
      Assert.notNull(valueRegex, "Header value pattern cannot be null.");
      this.name = Pattern.compile(nameRegex);
      this.value = Pattern.compile(valueRegex);
   }

   /**
    * Return a {@link Header} condition that matches against both header name and values.
    * 
    * @param nameRegex Regular expression matching the header name
    * @param valueRegex Regular expression matching the header value
    */
   public static Header matches(final String nameRegex, final String valueRegex)
   {
      return new Header(nameRegex, valueRegex);
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with a name matching the
    * given pattern. The header value is ignored.
    * 
    * @param nameRegex Regular expression matching the header name
    */
   public static Header exists(final String nameRegex)
   {
      return new Header(nameRegex, ".*");
   }

   /**
    * Return a {@link Header} condition that matches only against the existence of a header with value matching the
    * given pattern. The header name is ignored.
    * 
    * @param valueRegex Regular expression matching the header value
    */
   public static Header valueExists(final String valueRegex)
   {
      return new Header(".*", valueRegex);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event, final EvaluationContext context)
   {
      HttpServletRequest request = event.getRequest();
      for (String header : Collections.list(request.getHeaderNames()))
      {
         if (name.matcher(header).matches() && matchesValue(request, header))
         {
            return true;
         }
      }
      return false;
   }

   private boolean matchesValue(final HttpServletRequest request, final String header)
   {
      for (String contents : Collections.list(request.getHeaders(header)))
      {
         if (value.matcher(contents).matches())
         {
            return true;
         }
      }
      return false;
   }
}
