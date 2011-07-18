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

import java.util.regex.Pattern;

import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import com.ocpsoft.rewrite.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Path extends HttpCondition
{
   private final Pattern pattern;

   private Path(final String pattern)
   {
      Assert.notNull(pattern, "URL pattern must not be null.");
      this.pattern = Pattern.compile(pattern);
   }

   public static Path matches(final String pattern)
   {
      return new Path(pattern);
   }

   @Override
   public boolean evaluateHttp(final HttpServletRewrite event)
   {
      return pattern.matcher(event.getRequestURL()).matches();
   }

}