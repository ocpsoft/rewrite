/*
 * Copyright 2010 Lincoln Baxter, III
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

package com.ocpsoft.pretty.faces.rewrite.processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.Processor;

/**
 * A processor that replaces the current URL with the URL specified in {@link RewriteRule.getURL()}, and provides Regex
 * Backreferences into the match expression if provided.
 * <p/>
 * Example: <br/>
 * match="^/foo/(\w+)/$"<br/>
 * url="http://ocpsoft.com/$1/" ...s
 * 
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class UrlProcessor implements Processor
{

   private String process(final HttpServletRequest request, final HttpServletResponse response, final RewriteRule rule,
            final String url)
   {
      if ((url == null) || (rule.getUrl().length() == 0))
      {
         return url;
      }

      String result = url;

      if (rule.getMatch().length() > 0)
      {
         result = url.replaceAll(rule.getMatch(), rule.getUrl());
      }

      else
      {
         result = rule.getUrl();
      }

      return result;
   }

   public String processInbound(final HttpServletRequest request, final HttpServletResponse response,
            final RewriteRule rule,
            final String url)
   {
      return process(request, response, rule, url);
   }

   public String processOutbound(final HttpServletRequest request, final HttpServletResponse response,
            final RewriteRule rule, final String url)
   {
      return process(request, response, rule, url);
   }

}
