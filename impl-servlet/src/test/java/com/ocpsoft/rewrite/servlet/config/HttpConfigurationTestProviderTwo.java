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
package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.config.And;
import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationBuilder;
import com.ocpsoft.rewrite.config.ConfigurationProvider;
import com.ocpsoft.rewrite.config.Inbound;
import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HttpConfigurationTestProviderTwo implements ConfigurationProvider
{
   public static boolean performed = false;

   @Override
   public int priority()
   {
      return 1;
   }

   @Override
   public Configuration getConfiguration()
   {
      return ConfigurationBuilder.begin()
               .addRule()
               .setCondition(And.all(Inbound.only(), Path.matches("/path")))
               .setOperation(new Operation() {
                  @Override
                  public void perform(final Rewrite event)
                  {
                     ((HttpInboundServletRewrite) event).sendStatusCode(200);
                     performed = true;
                  }
               });
   }

}
