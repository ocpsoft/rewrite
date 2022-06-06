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
package org.ocpsoft.rewrite.servlet.wrapper;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
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
public class ResponseContentInterceptorIT extends RewriteIT
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteIT
               .getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsWebResource(new StringAsset("UPPERCASE"), "index.html")
               .addAsWebResource(new StringAsset("UPPERCASE"), "unbuffered.html")
               .addAsWebResource(new StringAsset("UPPERCASE"), "forward.html")
               .addAsServiceProvider(ConfigurationProvider.class, ResponseContentInterceptorTestProvider.class);
      return deployment;
   }

   @Test
   public void testResponseBufferingAppliesAllBuffers() throws Exception
   {
      HttpAction action = get("/index.html");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseContent()).isEqualTo("lowercase");
   }

   @Test
   public void testResponseBufferingOnlyAppliesWhenBuffersRegistered() throws Exception
   {
      HttpAction action = get("/unbuffered");
      assertThat(action.getStatusCode()).isEqualTo(201);
      assertThat(action.getResponseContent()).isEqualTo("UPPERCASE");
   }

   @Test
   public void testResponseBufferingAcceptedAfterForward() throws Exception
   {
      HttpAction action = get("/bufferforward");
      assertThat(action.getStatusCode()).isEqualTo(202);
      assertThat(action.getResponseContent()).isEqualTo("uppercase");
   }

   @Test
   public void testResponseBufferingRejectedAfterStreamAccessed() throws Exception
   {
      HttpAction action = get("/bufferfail");
      assertThat(action.getStatusCode()).isEqualTo(503);
   }
}
