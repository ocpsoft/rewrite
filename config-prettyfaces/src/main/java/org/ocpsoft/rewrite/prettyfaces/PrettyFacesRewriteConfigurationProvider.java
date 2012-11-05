/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ocpsoft.rewrite.prettyfaces;

import java.util.List;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PrettyFacesRewriteConfigurationProvider extends HttpConfigurationProvider
{
   
   public int priority()
   {
      return 1;
   }

   public Configuration getConfiguration(final ServletContext context)
   {
      ConfigurationBuilder builder = ConfigurationBuilder.begin();

      PrettyConfig config = (PrettyConfig) context.getAttribute(PrettyContext.CONFIG_KEY);
      if (config != null)
      {
         List<RewriteRule> rules = config.getGlobalRewriteRules();
         List<UrlMapping> mappings = config.getMappings();

         for (RewriteRule rule : rules) {
            builder.addRule(new InboundRewriteRuleAdaptor(rule));
         }

         for (UrlMapping mapping : mappings) {
            builder.addRule(new UrlMappingRuleAdaptor(mapping));
         }

         for (RewriteRule rule : rules) {
            builder.addRule(new OutboundRewriteRuleAdaptor(rule));
         }
      }
      return builder;
   }

}
