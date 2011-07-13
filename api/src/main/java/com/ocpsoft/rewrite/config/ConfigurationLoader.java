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
package com.ocpsoft.rewrite.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ocpsoft.rewrite.pattern.WeightedComparator;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.util.Iterators;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationLoader
{
   public static Configuration loadConfiguration()
   {
      @SuppressWarnings("unchecked")
      ServiceLoader<ConfigurationProvider> loader = ServiceLoader.load(ConfigurationProvider.class);
      List<ConfigurationProvider> providers = Iterators.asList(loader.iterator());

      Collections.sort(providers, new WeightedComparator());

      List<Configuration> configs = new ArrayList<Configuration>();
      for (ConfigurationProvider provider : providers) {
         configs.add(provider.getConfiguration());
      }

      ConfigurationBuilder result = ConfigurationBuilder.begin();

      for (Configuration configuration : configs) {
         for (Rule rule : configuration.getRules()) {
            result.addRule(rule);
         }
      }

      return result;
   }
}
