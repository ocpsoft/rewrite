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
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationBuilder implements Configuration
{
   private final List<Rule> rules = new ArrayList<Rule>();

   ConfigurationBuilder()
   {}

   @Override
   public List<Rule> getRules()
   {
      return rules;
   }

   public static ConfigurationBuilder instance()
   {
      return new ConfigurationBuilder();
   }

   public RuleBuilder rule()
   {
      RuleBuilder rule = new RuleBuilder(this);
      rules.add(rule);
      return rule;
   }

   public RuleBuilder rule(final Rule rule)
   {
      RuleBuilder builder = new RuleBuilder(this, rule);
      rules.add(builder);
      return builder;
   }
}
