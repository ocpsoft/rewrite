/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.servlet.ServletRegistration;
import org.ocpsoft.rewrite.servlet.spi.ServletRegistrationProvider;

/**
 * Implementation of {@link ServletRegistrationProvider} that uses {@link ServletContext#getServletRegistrations()} if a
 * Servlet 3.0 environment is detected.
 * 
 * @author Christian Kaltepoth
 */
public class Servlet3ServletRegistrationProvider implements ServletRegistrationProvider
{

   @Override
   public int priority()
   {
      return 10;
   }

   @Override
   public List<ServletRegistration> getServletRegistrations(ServletContext context)
   {

      if (context.getMajorVersion() >= 3) {

         List<ServletRegistration> result = new ArrayList<ServletRegistration>();

         for (javax.servlet.ServletRegistration r : context.getServletRegistrations().values()) {
            ServletRegistration registration = new ServletRegistration();
            registration.setClassName(r.getClassName());
            registration.addMappings(r.getMappings());
            result.add(registration);
         }

         return result;

      }

      // null tells the caller that cannot tell anything about the registrations
      return null;

   }

}
