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
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.servlet.ServletRoot;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
@Ignore // @ShouldThrowException doesn't seem to work on Tomcat and Glassfish
public class ConfigurationDeploymentTest
{
   @Deployment(testable = false)
   @ShouldThrowException(Exception.class)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest.getDeployment()
               .addPackages(true, ServletRoot.class.getPackage())
               .addAsResource(new StringAsset("org.ocpsoft.rewrite.servlet.config.NonExistentConfigProvider"),
                        "/META-INF/services/org.ocpsoft.rewrite.config.ConfigurationProvider");
      return deployment;
   }

   @Test
   public void testInvalidConfigProviderServiceFileFailsDeployment()
   {}

}
