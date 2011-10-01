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
package com.ocpsoft.rewrite.showcase.access;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.joda.time.DateTime;

import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.jodatime.JodaTime;
import com.ocpsoft.rewrite.config.jodatime.TimeCondition;
import com.ocpsoft.rewrite.servlet.config.DispatchType;
import com.ocpsoft.rewrite.servlet.config.Forward;
import com.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import com.ocpsoft.rewrite.servlet.config.Path;
import com.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AccessRewriteConfiguration extends HttpConfigurationProvider
{
   @Inject
   private TimerBean timerBean;

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
      return ConfigurationBuilder
               .begin()

               /*
                * Enable the root menu.
                */
               .addRule(Join.path("/").to("/index.xhtml").withInboundCorrection())

               /*
                * Time based access control (only grants access during the first half of each minute)
                */
               .addRule(Join.path("/timer").to("/timer.xhtml")
                        .when(JodaTime.matches(timeGranted))
                        .withInboundCorrection())

               /*
                * Deny access to anything except the index unless we matched one of the rules above. 
                */
               .defineRule()
               .when(Path.matches(".*").and(DispatchType.isRequest())
                        .andNot(Path.matches(".*javax.faces.resource.*"))
                        .andNot(Path.matches("/")))

               .perform(Forward.to("/accessDenied.xhtml"));
   }

   private final TimeCondition timeGranted = new TimeCondition() {
      @Override
      public boolean matches(final DateTime time)
      {
         return time.getSecondOfMinute() < 30;
      }
   };

   @Override
   public int priority()
   {
      return 0;
   }

}
