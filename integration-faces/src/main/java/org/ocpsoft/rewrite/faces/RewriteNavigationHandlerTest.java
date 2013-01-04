/*
 * Copyright 2010 Lincoln Baxter, III
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
package org.ocpsoft.rewrite.faces;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class RewriteNavigationHandlerTest
{

   @Test
   public void testGetNavigationCaseForNavigateOutcome()
   {

      /*
       * GIVEN a parent navigation handler that knows about a specific view
       */
      ConfigurableNavigationHandler parent = mock(ConfigurableNavigationHandler.class);
      FacesContext facesContext = mock(FacesContext.class);
      NavigationCase navigationCase = mock(NavigationCase.class);
      when(parent.getNavigationCase(facesContext, "something", "/viewId.xhtml")).thenReturn(navigationCase);

      /*
       * WHEN RewriteNavigationHandler processes a redirect outcome created by Navigate class
       */
      RewriteNavigationHandler handler = new RewriteNavigationHandler(parent);
      NavigationCase result = handler.getNavigationCase(facesContext, "something",
               "rewrite-redirect:/viewId.xhtml?param1=foo&param2=bar");

      /*
       * THEN it will return the correct navigation case from the parent handler
       */
      assertEquals(navigationCase, result);

   }

   @Test
   public void testHandleNavigationForNavigateOutcome() throws Exception
   {

      /*
       * GIVEN an ExternalContext whose encodeActionURL() method returns the URL unmodified
       */

      ExternalContext externalContext = mock(ExternalContext.class);
      when(externalContext.getRequestContextPath()).thenReturn("/myapp");
      when(externalContext.encodeActionURL(Matchers.anyString())).thenAnswer(new Answer<String>() {
         @Override
         public String answer(InvocationOnMock invocation) throws Throwable
         {
            return (String) invocation.getArguments()[0];
         }
      });
      FacesContext facesContext = mock(FacesContext.class);
      when(facesContext.getExternalContext()).thenReturn(externalContext);

      /*
       * WHEN handleNavigation() is called for a redirect outcome created from Navigate class
       */
      RewriteNavigationHandler handler = new RewriteNavigationHandler(null);
      handler.handleNavigation(facesContext, "something", "rewrite-redirect:/viewId.xhtml?param1=foo&param2=bar");

      /*
       * THEN a redirect to the correct URL is issued 
       */
      verify(externalContext).redirect("/myapp/viewId.xhtml?param1=foo&param2=bar");

   }

}
