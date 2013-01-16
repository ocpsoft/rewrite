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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.ocpsoft.rewrite.servlet.ServletRegistration;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Servlet3ServletRegistrationProviderTest
{

   @Test
   public void testShouldReturnNullForOldServletVersions()
   {
      // GIVEN a Servlet 2.5 container
      ServletContext servletContext = mock(ServletContext.class);
      when(servletContext.getMajorVersion()).thenReturn(2);

      // WHEN the provider is asked for the registrations
      Servlet3ServletRegistrationProvider provider = new Servlet3ServletRegistrationProvider();
      List<ServletRegistration> registrations = provider.getServletRegistrations(servletContext);

      // THEN it must return null
      assertNull(registrations);

   }

   @Test
   public void testShouldReturnEmptyListForNoRegistrations()
   {
      // GIVEN a Servlet 3.0 container
      ServletContext servletContext = mock(ServletContext.class);
      when(servletContext.getMajorVersion()).thenReturn(3);

      // AND no registrations
      when(servletContext.getServletRegistrations()).thenReturn(new HashMap());

      // WHEN the provider is asked for the registrations
      Servlet3ServletRegistrationProvider provider = new Servlet3ServletRegistrationProvider();
      List<ServletRegistration> result = provider.getServletRegistrations(servletContext);

      // THEN it must return an empty list
      assertNotNull(result);
      assertEquals(0, result.size());

   }

   @Test
   public void testShouldReturnCorrectRegistrationWithExistingRegistrations()
   {
      // GIVEN a Servlet 3.0 container
      ServletContext servletContext = mock(ServletContext.class);
      when(servletContext.getMajorVersion()).thenReturn(3);

      // AND one registration
      javax.servlet.ServletRegistration registration = mock(javax.servlet.ServletRegistration.class);
      when(registration.getClassName()).thenReturn("com.example.MyServlet");
      when(registration.getMappings()).thenReturn(Arrays.asList("*.jsf", "/faces/*"));
      Map registrationMap = new HashMap();
      registrationMap.put("test", registration);
      when(servletContext.getServletRegistrations()).thenReturn(registrationMap);

      // WHEN the provider is asked for the registrations
      Servlet3ServletRegistrationProvider provider = new Servlet3ServletRegistrationProvider();
      List<ServletRegistration> result = provider.getServletRegistrations(servletContext);

      // THEN it must return the correct registration
      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals("com.example.MyServlet", result.get(0).getClassName());
      assertEquals(Arrays.asList("*.jsf", "/faces/*"), result.get(0).getMappings());

   }

}
