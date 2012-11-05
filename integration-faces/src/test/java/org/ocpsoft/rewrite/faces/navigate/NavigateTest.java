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

public class NavigateTest
{

   @Test
   public void testViewWithoutParameters()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true",
               Navigate.to("/faces/some-view.xhtml")
                        .build());
   }

   @Test
   public void testViewWithoutRedirect()
   {
      assertEquals("/faces/some-view.xhtml",
               Navigate.to("/faces/some-view.xhtml")
                        .withoutRedirect()
                        .build());
   }

   @Test
   public void testViewWithSingleParameter()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true&param=value",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", "value")
                        .build());
   }

   @Test
   public void testViewWithSingleIntegerParameter()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true&param=123",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", 123)
                        .build());
   }

   @Test
   public void testViewWithMultipleParameters()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true&param=123&param=456",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", 123)
                        .with("param", 456)
                        .build());
   }

   @Test
   public void testViewWithEmptyStringParameters()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true&param=&param2=",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", "")
                        .with("param2", "")
                        .build());
   }

   @Test
   public void testViewWithNullParameter()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", null)
                        .build());
   }

   @Test
   public void testParameterEncoding()
   {
      assertEquals("/faces/some-view.xhtml?faces-redirect=true&param=a+b+%C3%A4",
               Navigate.to("/faces/some-view.xhtml")
                        .with("param", "a b \u00e4")
                        .build());
   }

}
