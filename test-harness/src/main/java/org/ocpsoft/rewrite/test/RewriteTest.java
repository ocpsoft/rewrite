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
package org.ocpsoft.rewrite.test;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@RunWith(Arquillian.class)
public class RewriteTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return getDeploymentNoWebXml()
               .setWebXML("jetty-web.xml");
   }

   public static WebArchive getDeploymentNoWebXml()
   {

      return ShrinkWrap
               .create(WebArchive.class, "rewrite-test.war")
               .addAsLibraries(getRewriteArchive())
      // .addAsLibraries(resolveDependencies("org.jboss.weld.servlet:weld-servlet:1.1.4.Final"))
      //
      // /*
      // * Set the EL implementation
      // */
      // .addAsLibraries(resolveDependencies("org.glassfish.web:el-impl:jar:2.2"))
      // .addAsServiceProvider(ExpressionFactory.class, ExpressionFactoryImpl.class)
      //
      // /*
      // * Set up container configuration
      // */
      // .addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml")
      // .addAsWebInfResource("jetty-env.xml", "jetty-env.xml")
      // .addAsWebInfResource("jetty-log4j.xml", "log4j.xml")
               ;
   }

   protected static JavaArchive getRewriteArchive()
   {
      return ShrinkWrap.create(JavaArchive.class, "rewrite-servlet.jar")
               .addAsResource(new File("../api/target/classes/org"))
               .addAsResource(new File("../impl-servlet/target/classes/org"))
               .addAsResource(new File("../impl-servlet/target/classes/META-INF"));
   }
}
