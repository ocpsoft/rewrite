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
package org.ocpsoft.rewrite.cdi.bridge;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.cdi.bind.BindingBean;
import org.ocpsoft.rewrite.cdi.bind.ExpressionLanguageTestConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
/*
 * TODO for some reason only the first CDI test run functions. look in to this
 */
@RunWith(Arquillian.class)
public class CdiMultipleFeaturesTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteTest
               .getDeployment()
               .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml")
               .addClasses(BindingBean.class, ExpressionLanguageTestConfigurationProvider.class, MockBean.class,
                        RewriteLifecycleEventObserver.class, ServiceEnricherTestConfigProvider.class);
   }

   /*
    * RewriteProviderBridge
    */
   @Test
   public void testRewriteProviderBridgeAcceptsChanges() throws Exception
   {
      HttpAction<HttpGet> action = get("/success");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testRewriteRedirect301() throws Exception
   {
      HttpAction<HttpGet> action = get("/redirect-301");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
      Assert.assertEquals("/outbound-rewritten", action.getCurrentContextRelativeURL());
   }

   /*
    * CdiServiceEnricher
    */
   @Test
   public void testCdiServiceEnricherProvidesEnrichment() throws Exception
   {
      HttpAction<HttpGet> action = get("/cdi/inject");
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   /*
    * CdiExpressionLanguageProvider
    */
   @Test
   public void testParameterExpressionBinding() throws Exception
   {
      HttpAction<HttpGet> action = get("/one/2");
      Assert.assertEquals("/result/2/one", action.getCurrentContextRelativeURL());
      Assert.assertEquals(200, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testParameterRegexValidationIgnoresInvalidInput1() throws Exception
   {
      HttpAction<HttpGet> action = get("/one/44");
      Assert.assertEquals("/one/44", action.getCurrentContextRelativeURL());
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testParameterRegexValidationIgnoresInvalidInput2() throws Exception
   {
      HttpAction<HttpGet> action = get("/one/two");
      Assert.assertEquals("/one/two", action.getCurrentContextRelativeURL());
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }
}
