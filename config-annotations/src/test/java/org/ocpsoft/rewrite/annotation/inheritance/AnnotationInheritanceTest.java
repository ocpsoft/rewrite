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
package org.ocpsoft.rewrite.annotation.inheritance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class AnnotationInheritanceTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .addAsLibrary(RewriteAnnotationTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteAnnotationTest.getRewriteCdiArchive())
               .addClass(FieldInheritanceBean.class)
               .addClass(FieldInheritanceSuperClass.class)
               .addClass(MethodInheritanceBean.class)
               .addClass(MethodInheritanceSuperClass.class)
               .addAsWebResource(new StringAsset(
                        "param1=[${fieldInheritanceBean.param1}], param2=[${fieldInheritanceBean.param2}]"),
                        "fields.jsp")
               .addAsWebResource(new StringAsset(
                        "${methodInheritanceBean.logEntries}]"),
                        "methods.jsp");
   }

   @Test
   public void testParametersInSuperClass() throws Exception
   {
      HttpAction<HttpGet> action = get("/fields?param1=foo&param2=bar");
      assertEquals(200, action.getStatusCode());
      assertThat("Parameter in sub class not injected",
               action.getResponseContent(), Matchers.containsString("param1=[foo]"));
      assertThat("Parameter in super class not injected",
               action.getResponseContent(), Matchers.containsString("param2=[bar]"));
   }

   @Test
   public void testMethodsInSuperClass() throws Exception
   {
      HttpAction<HttpGet> action = get("/methods");
      assertEquals(200, action.getStatusCode());
      assertThat("Method in sub class not invoked",
               action.getResponseContent(), Matchers.containsString("[action1 invoked]"));
      assertThat("Method in super class not invoked",
               action.getResponseContent(), Matchers.containsString("[action2 invoked]"));
   }

}
