/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.cdi.manager;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ocpsoft.rewrite.cdi.spi.BeanManagerProvider;

/**
 * A base for building a {@link BeanManagerProvider} backed by JNDI
 * 
 * @author Nicklas Karlsson
 */
public abstract class AbstractJndiBeanManagerProvider implements BeanManagerProvider
{
   public BeanManager getBeanManager()
   {
      try {
         return (BeanManager) new InitialContext().lookup(getLocation());
      }
      catch (NamingException e) {
         // No panic, it's just not there
      }
      return null;
   }

   protected abstract String getLocation();

}
