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

import org.junit.Test;
import org.ocpsoft.rewrite.faces.RewriteNavigationHandler;

import static org.assertj.core.api.Assertions.assertThat;

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
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml");
   }

   @Test
   public void testRedirectWithSingleParameter()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .with("param", "value")
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml?param=value");
   }

   @Test
   public void testRedirectWithSingleIntegerParameter()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .with("param", 123)
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml?param=123");
   }

   @Test
   public void testRedirectWithMultipleParameters()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .with("param", 123)
              .with("param", 456)
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml?param=123&param=456");
   }

   @Test
   public void testRedirectWithEmptyStringParameters()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .with("param", "")
              .with("param2", "")
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml?param=&param2=");
   }

   @Test
   public void testRedirectWithNullParameter()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .with("param", null)
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml");
   }

   @Test
   public void testRedirectParameterEncoding()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .with("param", "a b \u00e4")
              .build()).isEqualTo("rewrite-redirect:/faces/some-view.xhtml?param=a+b+%C3%A4");
   }

   @Test
   public void testNavigationWithoutParameters()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .build()).isEqualTo("/faces/some-view.xhtml");
   }

   @Test
   public void testNavigationWithSingleParameter()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .with("param", "value")
              .build()).isEqualTo("/faces/some-view.xhtml?param=value");
   }

   @Test
   public void testNavigationWithSingleIntegerParameter()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .with("param", 123)
              .build()).isEqualTo("/faces/some-view.xhtml?param=123");
   }

   @Test
   public void testNavigationWithMultipleParameters()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .with("param", 123)
              .with("param", 456)
              .build()).isEqualTo("/faces/some-view.xhtml?param=123&param=456");
   }

   @Test
   public void testNavigationWithEmptyStringParameters()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .with("param", "")
              .with("param2", "")
              .build()).isEqualTo("/faces/some-view.xhtml?param=&param2=");
   }

   @Test
   public void testNavigationWithNullParameter()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .with("param", null)
              .build()).isEqualTo("/faces/some-view.xhtml");
   }

   @Test
   public void testNavigationParameterEncoding()
   {
      assertThat(Navigate.to("/faces/some-view.xhtml")
              .withoutRedirect()
              .with("param", "a b \u00e4")
              .build()).isEqualTo("/faces/some-view.xhtml?param=a b \u00e4");
   }

}
