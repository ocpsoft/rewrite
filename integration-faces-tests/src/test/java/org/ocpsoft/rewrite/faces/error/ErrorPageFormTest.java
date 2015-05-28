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
package org.ocpsoft.rewrite.faces.error;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.faces.annotation.RewriteFacesAnnotationsTest;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.ocpsoft.rewrite.test.RewriteTestBase;

@RunWith(Arquillian.class)
public class ErrorPageFormTest extends RewriteTestBase
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteAnnotationArchive())
               .addAsLibrary(RewriteFacesAnnotationsTest.getRewriteFacesArchive())
               .addClass(ErrorBean.class)
               .addAsWebInfResource("error-web.xml", "web.xml")
               .addAsWebResource("error-page.xhtml", "error.xhtml");
   }

   @Test
   public void testNavigateWithSimpleString() throws Exception
   {
      HttpAction<HttpGet> client = get("/missingresource");
      assertThat(client.getCurrentURL(), endsWith("/missingresource"));
      Assert.assertEquals(404, client.getResponse().getStatusLine().getStatusCode());

      String content = client.getResponseContent();
      Assertions.assertThat(content)
               .matches("(?s).*action=\"" + client.getContextPath() + "/faces/error\\.xhtml.*");
   }

}
