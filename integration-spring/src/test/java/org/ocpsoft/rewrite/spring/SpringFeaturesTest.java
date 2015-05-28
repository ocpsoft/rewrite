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
package org.ocpsoft.rewrite.spring;

import org.junit.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 * This tests the basic features of the Spring integration module:
 * </p>
 * 
 * <ul>
 * <li>SpringExpressionLanguageProvider: Binding parameters to Spring beans</li>
 * <li>SpringServiceEnricher: {@link SpringExpressionLanguageProvider} requires {@link WebApplicationContext}</li>
 * <li>SpringServiceLocator: Automatic discovery of {@link SpringFeaturesConfigProvider}</li>
 * </ul>
 * 
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class SpringFeaturesTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeployment()
               .setWebXML("spring-web.xml")
               .addAsWebInfResource("applicationContext.xml")
               .addAsLibraries(resolveDependencies("org.springframework:spring-web:3.0.6.RELEASE"))
               .addClasses(SpringFeaturesBean.class, SpringFeaturesConfigProvider.class);
   }

   @Test
   public void testSpringFeatures() throws Exception
   {
      HttpAction<HttpGet> action = get("/name-christian");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("/hello/CHRISTIAN", action.getCurrentContextRelativeURL());
   }

}
