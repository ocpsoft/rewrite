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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;

public class Servlet3DispatcherTypeProviderTest
{

   @Test
   public void testShouldReturnCorrectDispatcherTypeForServlet3()
   {

      // GIVEN a Servlet 3.0 container
      ServletContext servletContext = mock(ServletContext.class);
      when(servletContext.getMajorVersion()).thenReturn(3);

      // AND a forwarded request
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getDispatcherType()).thenReturn(javax.servlet.DispatcherType.FORWARD);

      // WHEN the provider is asked for the DispatcherType
      DispatcherTypeProvider provider = new Servlet3DispatcherTypeProvider();
      DispatcherType dispatcherType = provider.getDispatcherType(request, servletContext);

      // THEN it should return FORWARD
      assertEquals(DispatcherType.FORWARD, dispatcherType);

   }

   @Test
   public void testShouldReturnNullForServlet25()
   {

      // GIVEN a Servlet 2.5 container
      ServletContext servletContext = mock(ServletContext.class);
      when(servletContext.getMajorVersion()).thenReturn(2);
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getDispatcherType()).thenThrow(new IllegalStateException("Call not allowed"));

      // WHEN the provider is asked for the DispatcherType
      DispatcherTypeProvider provider = new Servlet3DispatcherTypeProvider();
      DispatcherType dispatcherType = provider.getDispatcherType(request, servletContext);

      // THEN it should return null
      assertNull(dispatcherType);

   }

}
