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
package org.ocpsoft.rewrite.cdi.resolver;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteIT;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Christian Kaltepoth
 */
@RunWith(Arquillian.class)
public class CdiBeanNameResolverIT extends RewriteIT
{

   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return RewriteIT.getDeployment()
          // Necessary as of CDI 4.0 because we need bean-discovery-mode="all"
          .addAsWebInfResource(new File("src/main/resources/META-INF/beans.xml"))
          .addClasses(CdiBeanNameResolverBean.class, CdiBeanNameResolverConfigProvider.class);
   }

   @Test
   public void testCdiBeanNameResolver() throws Exception
   {
      HttpAction action = get("/name/christian");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getCurrentContextRelativeURL()).isEqualTo("/hello/CHRISTIAN");
   }

}
