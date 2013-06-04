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
package test.org.ocpsoft.rewrite.cdi.convert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import test.org.ocpsoft.rewrite.cdi.RewriteELTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Arquillian.class)
public class BindingConverterTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithFacesAndCDI()
               .addAsLibrary(RewriteELTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteELTest.getRewriteFacesArchive())
               .addAsLibrary(RewriteELTest.getRewriteCDIArchive())
               .addClass(BindingConverterBean.class)
               .addClass(AdvancedString.class)
               .addClass(AdvancedStringConverterById.class)
               .addClass(AdvancedStringConverterByType.class)
               .addAsWebResource("convert-simple.xhtml", "convert.xhtml");
   }

   @Test
   public void testFacesConverterByIdAndByType() throws Exception
   {

      HtmlPage page = getWebClient("/convert/foo/").getPage();

      String content = page.getWebResponse().getContentAsString();
      assertContains(content, "byId = [foo] with length 3");
      assertContains(content, "byType = [foo] with length 3");

   }

}