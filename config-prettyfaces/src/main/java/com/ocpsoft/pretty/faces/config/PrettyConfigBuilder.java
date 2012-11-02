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
package com.ocpsoft.pretty.faces.config;

import java.util.LinkedList;
import java.util.List;

import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;

/**
 * Pretty Faces configuration builder. Accepts configuration elements (
 * {@link #addMapping(UrlMapping)}) and builds the configuration (
 * {@link #build()}).
 * 
 * @author Aleksei Valikov
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyConfigBuilder
{
   private final List<UrlMapping> mappings = new LinkedList<UrlMapping>();
   private final List<RewriteRule> rewriteRules = new LinkedList<RewriteRule>();

   public void addFromConfig(PrettyConfig config)
   {
      if (config != null)
      {
         mappings.addAll(config.getMappings());
         rewriteRules.addAll(config.getGlobalRewriteRules());
      }
   }

   public void addMapping(final UrlMapping mapping)
   {
      if (mapping != null)
      {
         mappings.add(mapping);
      }
   }

   public void addRewriteRule(final RewriteRule rule)
   {
      if (rule != null)
      {
         rewriteRules.add(rule);
      }
   }

   public PrettyConfig build()
   {
      final PrettyConfig config = new PrettyConfig();

      config.setMappings(mappings);
      config.setGlobalRewriteRules(rewriteRules);

      return config;
   }
}
