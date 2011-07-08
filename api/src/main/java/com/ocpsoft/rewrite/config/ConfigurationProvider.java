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

import com.ocpsoft.rewrite.pattern.Weighted;

/**
 * Provider configuration to the Rewrite runtime environment.
 * 
 * Additional configuration providers my be specified by providing a service activator file containing the name of your
 * implementations:
 * <p>
 * <code> /META-INF/services/com.ocpsoft.rewrite.config.ConfigurationProvider<br> 
 * 
 * --------------<br>
 * com.example.ConfigurationProviderImpl</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ConfigurationProvider extends Weighted
{
   /**
    * Return the additional configuration.
    */
   public Configuration getConfiguration();
}
