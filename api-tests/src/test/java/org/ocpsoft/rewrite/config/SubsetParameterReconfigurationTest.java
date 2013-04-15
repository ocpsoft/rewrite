/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.config;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.ocpsoft.rewrite.Root;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SubsetParameterReconfigurationTest
{
   @Deployment(testable = false)
   @ShouldThrowException(DeploymentException.class)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, Root.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class,
                        SubsetParameterReconfigurationTestProvider.class);
      return deployment;
   }

   @Test
   public void testSubsetParameterReconfigurationIsDeploymentFailure() throws Exception
   {}

}
