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
package org.ocpsoft.rewrite.faces.actionurl;

import static org.junit.Assert.assertThat;

import java.net.URL;

import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Test for reproducing #166.
 * 
 * @author Christian Kaltepoth
 * @see https://github.com/ocpsoft/rewrite/issues/166
 */
@RunWith(Arquillian.class)
public class ActionUrlAfterPostbackTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(ActionUrlAfterPostbackBean.class)
               .addAsWebResource("actionurl-page1.xhtml", "page1.xhtml")
               .addAsWebResource("actionurl-page2.xhtml", "page2.xhtml");
   }

   @ArquillianResource
   private URL baseUrl;

   @Test
   public void formUrlShouldBeSameAsRenderedView() throws Exception
   {

      HtmlUnitDriver driver = new HtmlUnitDriver();

      // Load page1 and click on the button which will render page2 (without faces-redirect=true)
      driver.get(baseUrl + "page1.jsf");
      driver.findElementById("form1:goto-page2").click();

      // The form URL on page2 should NOT point to page1
      String actionUrl = driver.findElementById("form2").getAttribute("action");
      assertThat(actionUrl, Matchers.containsString("page2"));

      // Click the button which creates a FacesMessage. Due to #166 this fails with a ViewExpiredException
      driver.findElementById("form2:create-message").click();
      assertThat(driver.getPageSource(), Matchers.containsString("Action method got executed"));

   }

}