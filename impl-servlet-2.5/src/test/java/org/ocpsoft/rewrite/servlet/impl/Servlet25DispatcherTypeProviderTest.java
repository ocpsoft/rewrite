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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.ocpsoft.rewrite.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider;

public class Servlet25DispatcherTypeProviderTest
{

   @Test
   public void testShouldReturnRequestDispatcherTypeForNoAttributes()
   {

      // GIVEN a request without any attributes set
      HttpServletRequest request = mock(HttpServletRequest.class);

      // WHEN the provider is asked for the DispatcherType
      DispatcherTypeProvider provider = new Servlet25DispatcherTypeProvider();
      DispatcherType dispatcherType = provider.getDispatcherType(request, mock(ServletContext.class));

      // THEN it should return REQUEST
      assertEquals(DispatcherType.REQUEST, dispatcherType);

   }

   @Test
   public void testShouldReturnIncludeDispatcherTypeWithCorrectAttributeSet()
   {

      // GIVEN a request with the INCLUDE attribute set
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getAttribute("javax.servlet.include.request_uri")).thenReturn("/some/url");

      // WHEN the provider is asked for the DispatcherType
      DispatcherTypeProvider provider = new Servlet25DispatcherTypeProvider();
      DispatcherType dispatcherType = provider.getDispatcherType(request, mock(ServletContext.class));

      // THEN it should return INCLUDE
      assertEquals(DispatcherType.INCLUDE, dispatcherType);

   }

   @Test
   public void testShouldReturnForwardDispatcherTypeWithCorrectAttributeSet()
   {

      // GIVEN a request with the FORWARD attribute set
      HttpServletRequest request = mock(HttpServletRequest.class);
      when(request.getAttribute("javax.servlet.forward.request_uri")).thenReturn("/some/url");

      // WHEN the provider is asked for the DispatcherType
      DispatcherTypeProvider provider = new Servlet25DispatcherTypeProvider();
      DispatcherType dispatcherType = provider.getDispatcherType(request, mock(ServletContext.class));

      // THEN it should return INCLUDE
      assertEquals(DispatcherType.FORWARD, dispatcherType);

   }

}
