package org.ocpsoft.rewrite.faces;

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

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTestBase;

import com.google.common.base.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PhaseOperationTest extends RewriteTestBase
{
   @Deployment(testable = true)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTestBase
               .getDeploymentNoWebXml()
               .setWebXML("faces-web.xml")
               .addPackages(true, FacesRoot.class.getPackage())
               .addAsLibraries(resolveDependencies("org.glassfish:javax.faces:jar:2.1.7"))
               .addAsWebInfResource("faces-config.xml","faces-config.xml")
               .addAsWebResource("empty.xhtml", "empty.xhtml")
               .addAsServiceProvider(ConfigurationProvider.class, PhaseOperationTestConfigurationProvider.class);

      return deployment;
   }

   @Test
   public void testDeferOperationRestoreView()
   {
      HttpAction<HttpGet> action = get("/empty.xhtml?adf=blah");
      String content = action.getResponseContent();
      Assert.assertTrue(Strings.isNullOrEmpty(content));
      // TODO why is content empty here, but null in below test?
      Assert.assertEquals(203, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testDeferOperationRenderResponse()
   {
      HttpAction<HttpGet> action = get("/render_response");
      String content = action.getResponseContent();
      Assert.assertTrue(Strings.isNullOrEmpty(content));
      // TODO why is content null here, but not in above test?
      Assert.assertEquals(204, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testPhaseBindingDefersValue()
   {
      HttpAction<HttpGet> action = get("/binding/lincoln");
      String content = action.getResponseContent();
      Assert.assertTrue(Strings.isNullOrEmpty(content));
      // TODO why is content null here, but not in above test?
      Assert.assertEquals(205, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("lincoln", action.getResponseHeaderValues("Value").get(0));
   }

}