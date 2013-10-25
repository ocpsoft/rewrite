/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.prettyfaces.dynaview;

import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.HttpGet;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.category.IgnoreForWildfly;
import org.ocpsoft.rewrite.prettyfaces.PrettyFacesTestBase;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

/**
 * Ignored for Wildfly Beta1 because it ships with Mojarra 2.2.3 which ALWAYS appends 'jftfdi' and 'jffi' parameters.
 * This seems to break our test asserts.
 * 
 * @see https://java.net/jira/browse/JAVASERVERFACES-3054
 */
@RunWith(Arquillian.class)
@Category(IgnoreForWildfly.class)
public class AmbiguousViewidTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive createDeployment()
   {
      return PrettyFacesTestBase.getDeployment()
               .addClass(AmbiguousViewIdBean.class)
               .addAsWebResource("dynaview/ambiguousViewId.xhtml", "index.xhtml")
               .addAsWebInfResource("dynaview/ambiguous-pretty-config.xml", "pretty-config.xml");
   }

   @Test
   public void testLinkGenerationSelectsCorrectMapping() throws Exception
   {
      HttpAction<HttpGet> action = get("/foo");

      Assert.assertEquals(200, action.getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("action=\"" + action.getContextPath() + "/foo\""));

      assertThat(action.getResponseContent(), Matchers.containsString("href=\"" + action.getContextPath() + "/foo\""));
      assertThat(action.getResponseContent(), Matchers.containsString("href=\"" + action.getContextPath() + "/bar\""));
      assertThat(action.getResponseContent(), Matchers.containsString("href=\"" + action.getContextPath() + "/baz\""));
   }

   @Test
   public void testRendersCorrectURLForDynaview() throws Exception
   {
      HttpAction<HttpGet> action = get("/baz");

      Assert.assertEquals(200, action.getStatusCode());
      assertThat(action.getResponseContent(), Matchers.containsString("action=\"" + action.getContextPath() + "/baz\""));

      assertThat(action.getResponseContent(), Matchers.containsString("href=\"" + action.getContextPath() + "/foo\""));
      assertThat(action.getResponseContent(), Matchers.containsString("href=\"" + action.getContextPath() + "/bar\""));
      assertThat(action.getResponseContent(), Matchers.containsString("href=\"" + action.getContextPath() + "/baz\""));
   }
}
