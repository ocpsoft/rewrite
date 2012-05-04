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
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.mock.MockBinding;

import com.sun.el.ExpressionFactoryImpl;

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
      WebArchive archive = getDeploymentNoWebXml();
      if(isJetty()) {
         archive.setWebXML("jetty-web.xml");
      }
      return archive;
   }

   public static WebArchive getDeploymentNoWebXml()
   {

      WebArchive archive = ShrinkWrap
               .create(WebArchive.class, "rewrite-test.war")
               .addPackages(true, MockBinding.class.getPackage())
               .addAsLibraries(resolveDependencies("org.ocpsoft.logging:logging-api:1.0.1.Final"))
               .addAsLibraries(getRewriteArchive())
               .addAsLibraries(getCurrentArchive());

      // Jetty specific stuff
      if(isJetty()) {
         
          archive.addAsLibraries(resolveDependencies("org.jboss.weld.servlet:weld-servlet:1.1.4.Final"));
         
          /*
          * Set the EL implementation
          */
         archive.addAsLibraries(resolveDependencies("org.glassfish.web:el-impl:jar:2.2"));
         archive.add(new StringAsset("com.sun.el.ExpressionFactoryImpl"),
                  "/WEB-INF/classes/META-INF/services/javax.el.ExpressionFactory");
         
          /*
          * Set up container configuration
          */
          archive.addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml");
          archive.addAsWebInfResource("jetty-env.xml", "jetty-env.xml");
          archive.addAsWebInfResource("jetty-log4j.xml", "log4j.xml");
          
      }
      
      System.out.println(archive.toString(true));
      
      return archive;
   }
   
   protected static boolean isJetty() {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      try {
         classLoader.loadClass("org.jboss.arquillian.container.jetty.embedded_7.JettyEmbeddedContainer");
         return true;
      }
      catch (ClassNotFoundException e) {
         return false;
      }
   }

   protected static JavaArchive getCurrentArchive()
   {
      File classes = new File("target/classes/org");
      File metaInf = new File("target/classes/META-INF");

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-current-module.jar");

      if (classes.exists())
         archive.addAsResource(classes);
      if (metaInf.exists())
         archive.addAsResource(metaInf);
      
      return archive.addAsResource(new StringAsset("placeholder"), "README");
   }

   protected static JavaArchive getRewriteArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-servlet.jar")

               .addAsResource(new File("../api/target/classes/org"))
               .addAsResource(new File("../api-el/target/classes/org"))
               .addAsResource(new File("../api-el/target/classes/META-INF"))
               .addAsResource(new File("../impl-config/target/classes/org"))
               .addAsResource(new File("../api-servlet/target/classes/org"))
               .addAsResource(new File("../impl-servlet/target/classes/org"))
               .addAsResource(new File("../impl-servlet/target/classes/META-INF"));

      return archive;
   }
}
