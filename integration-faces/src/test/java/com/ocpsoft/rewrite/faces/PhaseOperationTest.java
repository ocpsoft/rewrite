package com.ocpsoft.rewrite.faces;

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
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.rewrite.test.HttpAction;
import com.ocpsoft.rewrite.test.RewriteTestBase;

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
               .addPackages(true, ADFRoot.class.getPackage())
               .addAsLibraries(resolveDependencies("org.glassfish:javax.faces:jar:2.1.7"))
               .addAsWebInfResource("META-INF/faces-config.xml","faces-config.xml")
               .addAsWebResource("empty-view.xhtml","empty.xhtml")
               .addAsResource(
                        new StringAsset("com.ocpsoft.rewrite.faces.PhaseOperationTestConfigurationProvider"),
                        "/META-INF/services/com.ocpsoft.rewrite.config.ConfigurationProvider");
      
      return deployment;
   }

   @Test
   public void testUrlMappingConfiguration()
   {
      HttpAction<HttpGet> action = get("/empty.xhtml?adf=blah");
      String content = action.getResponseContent();
      Assert.assertTrue(content.contains(">Empty"));
      Assert.assertEquals(203, action.getResponse().getStatusLine().getStatusCode());
   }

}