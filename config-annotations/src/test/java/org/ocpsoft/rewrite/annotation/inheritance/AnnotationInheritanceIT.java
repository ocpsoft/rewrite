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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.RewriteAnnotationTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;
import org.ocpsoft.rewrite.test.RewriteITBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class AnnotationInheritanceIT extends RewriteITBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeploymentWithCDI()
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
      HttpAction action = get("/fields?param1=foo&param2=bar");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).as("Parameter in sub class not injected").contains("param1=[foo]");
      assertThat(action.getResponseContent()).as("Parameter in super class not injected").contains("param2=[bar]");
   }

   @Test
   public void testMethodsInSuperClass() throws Exception
   {
      HttpAction action = get("/methods");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).as("Method in sub class not invoked").contains("[action1 invoked]");
      assertThat(action.getResponseContent()).as("Method in super class not invoked").contains("[action2 invoked]");
   }

}
