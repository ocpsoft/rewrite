/*
 * Copyright 2011 <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
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
package org.ocpsoft.rewrite.servlet.config;

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
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
@RunWith(Arquillian.class)
public class RequestNullBindingIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT
               .getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, RequestNullBindingTestProvider.class);
      return deployment;
   }

   //"123" should be converted to 123L, OK
   @Test
   public void testNotNullBinding() throws Exception
   {
      HttpAction action = get("/foo/123");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   //"abc" can't be converted to Long so we're expecting an error 500 here.
   @Test
   public void testNonLongBinding() throws Exception
   {
      HttpAction action = get("/foo/abc");
      assertThat(action.getStatusCode()).isEqualTo(500);
   }
   
   // "" should be converted to null
   @Test
   public void testNullBinding1() throws Exception
   {
      HttpAction action = get("/foo/");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   // idem
   @Test
   public void testNullBinding2() throws Exception
   {
      HttpAction action = get("/bar//");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }
}