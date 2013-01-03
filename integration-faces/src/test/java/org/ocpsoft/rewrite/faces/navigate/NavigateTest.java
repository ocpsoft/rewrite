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
package org.ocpsoft.rewrite.faces.navigate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.ocpsoft.rewrite.faces.RewriteNavigationHandler;

/**
 * Test method names:
 * 
 * <ul>
 * <li>testRedirect*(): redirect outcome processed by {@link RewriteNavigationHandler}</li>
 * <li>testNavigatiom*(): standard JSF 2.0 implicit navigation outcome without redirect</li>
 * </ul>
 * 
 * @author ck
 * 
 */
public class NavigateTest
{

   @Test
   public void testRedirectWithoutParameters()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml",
               Navigate.to("/faces/some-view.xhtml")
                        .build());
   }

   @Test
   public void testRedirectWithSingleParameter()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml?param=value",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", "value")
                        .build());
   }

   @Test
   public void testRedirectWithSingleIntegerParameter()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml?param=123",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", 123)
                        .build());
   }

   @Test
   public void testRedirectWithMultipleParameters()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml?param=123&param=456",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", 123)
                        .with("param", 456)
                        .build());
   }

   @Test
   public void testRedirectWithEmptyStringParameters()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml?param=&param2=",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", "")
                        .with("param2", "")
                        .build());
   }

   @Test
   public void testRedirectWithNullParameter()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", null)
                        .build());
   }

   @Test
   public void testRedirectParameterEncoding()
   {
      assertEquals("rewrite-redirect:/faces/some-view.xhtml?param=a+b+%C3%A4",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", "a b \u00e4")
                        .build());
   }

   @Test
   public void testNavigationWithoutParameters()
   {
      assertEquals("/faces/some-view.xhtml",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .build());
   }

   @Test
   public void testNavigationWithSingleParameter()
   {
      assertEquals("/faces/some-view.xhtml?param=value",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .with("param", "value")
                        .build());
   }

   @Test
   public void testNavigationWithSingleIntegerParameter()
   {
      assertEquals("/faces/some-view.xhtml?param=123",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .with("param", 123)
                        .build());
   }

   @Test
   public void testNavigationWithMultipleParameters()
   {
      assertEquals("/faces/some-view.xhtml?param=123&param=456",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .with("param", 123)
                        .with("param", 456)
                        .build());
   }

   @Test
   public void testNavigationWithEmptyStringParameters()
   {
      assertEquals("/faces/some-view.xhtml?param=&param2=",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .with("param", "")
                        .with("param2", "")
                        .build());
   }

   @Test
   public void testNavigationWithNullParameter()
   {
      assertEquals("/faces/some-view.xhtml",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .with("param", null)
                        .build());
   }

   @Test
   public void testNavigationParameterEncoding()
   {
      assertEquals("/faces/some-view.xhtml?param=a b \u00e4",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .with("param", "a b \u00e4")
                        .build());
   }

}
