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
package org.ocpsoft.rewrite.servlet.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class JoinConfigurationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, JoinConfigurationProvider.class);
      return deployment;
   }

   @Test
   public void testUrlMappingConfiguration() throws Exception
   {
      HttpAction action = get("/p/rewrite");
      assertThat(action.getStatusCode()).isEqualTo(203);

      assertThat(action.getResponseHeaderValues("Project").get(0)).isEqualTo("rewrite");
      assertThat(action.getResponseHeaderValues("Encoded-URL").get(0)).isEqualTo(action.getContextPath() + "/p/rewrite");
   }

   @Test
   public void testUrlMappingConfigurationWithoutInboundCorrection() throws Exception
   {
      HttpAction action = get("/viewProject.xhtml");
      assertThat(action.getStatusCode()).isEqualTo(404);

      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/viewProject.xhtml");
   }

   @Test
   public void testUrlMappingConfigurationWithInboundCorrection() throws Exception
   {
      HttpAction action = get("/list.xhtml?p1=foo&p2=bar");
      assertThat(action.getStatusCode()).isEqualTo(204);

      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/foo/bar");
   }

   @Test
   public void testSubstitutionWithExtraQueryParams() throws Exception
   {
      HttpAction action = get("/1/querypath/2/?in=out");
      assertThat(action.getStatusCode()).isEqualTo(207);

      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/1/querypath/2/?in=out");
      assertThat(action.getResponseHeaderValues("InboundURL").get(0)).isEqualTo(getContextPath() + "/1-query.xhtml?in=out");
      assertThat(action.getResponseHeaderValues("OutboundURL")
              .get(0)).isEqualTo("/12345/querypath/cab/?foo=bar&kitty=meow");
   }

}