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
package org.ocpsoft.rewrite.spring.resolver;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class SpringBeanNameResolverTest extends RewriteTest
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return ShrinkWrap
               .create(WebArchive.class, "rewrite-test.war")
               .addAsLibraries(getRewriteArchive())
               .setWebXML("jetty-web-spring.xml")
               .addAsResource("jetty-log4j.xml", ArchivePaths.create("/log4j.xml"))
               .addAsWebInfResource("applicationContext.xml")
               .addClass(SpringBeanNameResolverBean.class)
               .addClass(SpringBeanNameResolverConfigProvider.class);
   }

   @Test
   public void testSpringBeanNameResolver()
   {
      HttpAction<HttpGet> action = get("/name/christian");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("/hello/CHRISTIAN", action.getCurrentContextRelativeURL());
   }

}
