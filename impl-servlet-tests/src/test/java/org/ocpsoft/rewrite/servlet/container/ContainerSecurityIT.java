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
package org.ocpsoft.rewrite.servlet.container;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.servlet.ServletRoot;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ContainerSecurityIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT.getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsWebInfResource("org/ocpsoft/rewrite/servlet/container/web.xml")
               .addAsWebResource("org/ocpsoft/rewrite/servlet/container/login.html")
               .addAsWebResource("org/ocpsoft/rewrite/servlet/container/login-fail.html")
               .addAsWebResource("org/ocpsoft/rewrite/servlet/container/home.html", "/user/home.html")
               .addAsServiceProvider(ConfigurationProvider.class, ContainerSecurityTestProvider.class);

      return deployment;
   }

   @Test
   public void testContainerSecurityFunctionsWithRewriteJoin() throws Exception
   {
      HttpAction action = get("/user/home");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("LOGIN PAGE");
   }

   @Test
   public void testContainerSecurityFunctionsWithRewriteUnHandled() throws Exception
   {
      HttpAction action = get("/user/x");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).contains("LOGIN PAGE");
   }
}
