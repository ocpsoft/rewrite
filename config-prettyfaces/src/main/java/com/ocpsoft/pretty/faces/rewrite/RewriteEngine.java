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

package com.ocpsoft.pretty.faces.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.processor.CaseProcessor;
import com.ocpsoft.pretty.faces.rewrite.processor.CustomClassProcessor;
import com.ocpsoft.pretty.faces.rewrite.processor.RegexProcessor;
import com.ocpsoft.pretty.faces.rewrite.processor.TrailingSlashProcessor;
import com.ocpsoft.pretty.faces.rewrite.processor.UrlProcessor;

/**
 * Process URL rewrites based on configuration
 * 
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class RewriteEngine
{
   private static List<Processor> processors;

   static
   {
      List<Processor> list = new ArrayList<Processor>();
      list.add(new RegexProcessor());
      list.add(new CaseProcessor());
      list.add(new TrailingSlashProcessor());
      list.add(new CustomClassProcessor());
      list.add(new UrlProcessor());
      processors = Collections.unmodifiableList(list);
   }

   /**
    * Rewrite the given URL using the provided {@link RewriteRule} object as a set of rules.
    * 
    * @return The rewritten URL, or the unchanged URL if no action was taken.
    */
   public String processInbound(final HttpServletRequest request, final HttpServletResponse response,
            final RewriteRule rule, final String url)
   {
      String result = url;
      if ((rule != null) && rule.isInbound() && rule.matches(url))
      {
         for (Processor p : processors)
         {
            result = p.processInbound(request, response, rule, result);
         }
      }
      return result;
   }

   /**
    * Rewrite the given URL using the provided {@link RewriteRule} object. Process the URL only if the rule is set to
    * outbound="true"
    * 
    * @return The rewritten URL, or the unchanged URL if no action was taken.
    */
   public String processOutbound(final HttpServletRequest request, final HttpServletResponse response,
            final RewriteRule rule, final String url)
   {
      String result = url;
      if ((rule != null) && rule.isOutbound() && rule.matches(url))
      {
         for (Processor p : processors)
         {
            result = p.processOutbound(request, response, rule, result);
         }
      }
      return result;
   }

}
