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

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;

import org.ocpsoft.rewrite.cdi.spi.BeanManagerProvider;

/**
 * A runtime exception that is thrown when the attempt to resolve the BeanManager using the {@link BeanManagerProvider}
 * service fails to locate the {@link BeanManager}.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class BeanManagerUnavailableException extends RuntimeException
{
   private static final long serialVersionUID = 1435183932012902556L;

   private List<BeanManagerProvider> providers;

   public BeanManagerUnavailableException(List<BeanManagerProvider> providers)
   {
      this.providers = providers;
   }

   public List<BeanManagerProvider> getProviders()
   {
      return providers;
   }

   public String getProvidersAsString()
   {
      StringBuilder out = new StringBuilder();
      int i = 0;
      for (BeanManagerProvider provider : providers) {
         if (i > 0) {
            out.append(", ");
         }
         out.append(provider.getClass().getName());
         out.append("(");
         out.append(provider.priority());
         out.append(")");
         i++;
      }
      return out.toString();
   }

   @Override
   public String getMessage()
   {
      return "Failed to locate BeanManager using any of these providers: " + getProvidersAsString();
   }
}
