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

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.mock.MockBinding;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class RewriteTest extends RewriteTestBase
{
   public static WebArchive getDeployment()
   {
      return getDeployment("rewrite-test.war");
   }

   public static WebArchive getDeployment(String name)
   {
      WebArchive archive = getDeploymentNoWebXml(name);
      return archive;
   }

   public static WebArchive getDeploymentWithCDI()
   {
      WebArchive archive = getDeployment();
      archive.addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml");
      return archive;
   }

   public static WebArchive getDeploymentWithFacesAndCDI()
   {
      WebArchive archive = getDeployment();
      archive.addAsWebInfResource(new StringAsset("<beans/>"), "beans.xml");
      archive.addAsWebInfResource(new StringAsset("<faces-config version=\"2.0\"/>"), "faces-config.xml");
      return archive;
   }

   public static WebArchive getDeploymentNoWebXml()
   {
      return getDeploymentNoWebXml("rewrite-test.war");
   }

   public static WebArchive getDeploymentNoWebXml(String name)
   {
      if (isJetty() && "ROOT.war".equals(name))
         name = ".war";

      WebArchive archive = ShrinkWrap
               .create(WebArchive.class, name)
               .addPackages(true, MockBinding.class.getPackage())
               .addAsLibraries(resolveDependencies("org.ocpsoft.logging:logging-api"))
               .addAsLibraries(getRewriteArchive())
               .addAsLibraries(getRewriteConfigArchive())
               .addAsLibraries(getRewriteAnnotationsArchive())
               .addAsLibraries(getContainerArchive())
               .addAsLibraries(getCurrentArchive());

      // Jetty specific stuff
      if (isJetty()) {

         /*
         * Set the EL implementation
         */
         archive.add(new StringAsset("com.sun.el.ExpressionFactoryImpl"),
                  "/WEB-INF/classes/META-INF/services/javax.el.ExpressionFactory");

         /*
         * Set up container configuration
         */
         archive.addAsWebInfResource("jetty-env.xml", "jetty-env.xml");
         archive.addAsWebInfResource("jetty-log4j.xml", "log4j.xml");

      }

      // Tomcat specific stuff
      if (isTomcat()) {

         // setup Weld
         if (isWeld()) {
            archive.addAsLibraries(resolveDependencies("org.jboss.weld:weld-core:1.1.9.Final"));
            archive.addAsLibraries(resolveDependencies("org.jboss.weld.servlet:weld-servlet-core:1.1.9.Final"));
            archive.addAsWebResource("tomcat-weld-context.xml", "META-INF/context.xml");
         }

         // setup OWB
         if (isOWB()) {
            archive.addAsLibraries(resolveDependencies("javax.enterprise:cdi-api:1.0-SP4"));
            archive.addAsLibraries(resolveDependencies("org.apache.openwebbeans:openwebbeans-impl:1.1.5"));
            archive.addAsLibraries(resolveDependencies("org.apache.openwebbeans:openwebbeans-web:1.1.5"));
            archive.addAsLibraries(resolveDependencies("org.apache.openwebbeans:openwebbeans-spi:1.1.5"));
            archive.addAsLibraries(resolveDependencies("org.apache.openwebbeans:openwebbeans-resource:1.1.5"));
            archive.addAsWebResource("tomcat-owb-context.xml", "META-INF/context.xml");
         }

         // setup Mojarra
         archive.addAsLibraries(resolveDependencies("org.glassfish:javax.faces:2.1.13"));
         archive.addAsLibraries(resolveDependencies("javax.servlet:jstl:1.2"));

      }

      return archive;
   }

   public static boolean isJetty()
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      try {
         classLoader.loadClass("org.jboss.arquillian.container.jetty.embedded_7.JettyEmbeddedContainer");
         return true;
      }
      catch (ClassNotFoundException e) {
         return false;
      }
   }

   public static boolean isTomcat()
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      try {
         classLoader.loadClass("org.jboss.arquillian.container.tomcat.managed_7.TomcatManagedContainer");
         return true;
      }
      catch (ClassNotFoundException e) {
         return false;
      }
   }

   public static boolean isWeld()
   {
      return "weld".equalsIgnoreCase(System.getProperty("rewrite.test.cdi"));
   }

   public static boolean isOWB()
   {
      return "owb".equalsIgnoreCase(System.getProperty("rewrite.test.cdi"));
   }

   protected static JavaArchive getContainerArchive()
   {

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-container-module.jar");

      if (isJetty())
      {
         archive.addAsManifestResource("jetty-web-fragment.xml", "web-fragment.xml");
      }

      if (isTomcat())
      {
         if (isWeld()) {
            archive.addAsManifestResource("tomcat-weld-web-fragment.xml", "web-fragment.xml");
         }
         if (isOWB()) {
            archive.addAsManifestResource("tomcat-owb-web-fragment.xml", "web-fragment.xml");
         }
      }

      return archive.addAsResource(new StringAsset("placeholder"), "README");
   }

   protected static JavaArchive getCurrentArchive()
   {
      File org = new File("target/classes/org");
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-current-module.jar");

      if (!org.getAbsolutePath().contains("impl-servlet") && !org.getAbsolutePath().contains("config-servlet"))
      {
         addAsResource(archive, org);
         addAsResource(archive, new File("target/classes/META-INF"));
      }

      return archive.addAsResource(new StringAsset("placeholder"), "README");
   }

   protected static JavaArchive getRewriteArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-servlet.jar");

      addAsResource(archive, new File("../api/target/classes/org"));
      addAsResource(archive, new File("../api-el/target/classes/org"));
      addAsResource(archive, new File("../api-el/target/classes/META-INF"));
      addAsResource(archive, new File("../impl/target/classes/org"));
      addAsResource(archive, new File("../impl/target/classes/META-INF"));
      addAsResource(archive, new File("../addressbuilder/target/classes/org"));
      addAsResource(archive, new File("../api-servlet/target/classes/org"));

      /*
       * We have to manually create the META-NF/services entry for ServletRegistrationProvider
       * and DispatcherTypeProvider, because the existing ones from the two modules overwrite 
       * each other when added to the archive.
       */
      archive.addAsServiceProvider("org.ocpsoft.rewrite.servlet.spi.ServletRegistrationProvider",
               "org.ocpsoft.rewrite.servlet.impl.Servlet3ServletRegistrationProvider",
               "org.ocpsoft.rewrite.servlet.impl.WebXmlServletRegistrationProvider");
      archive.addAsServiceProvider("org.ocpsoft.rewrite.servlet.spi.DispatcherTypeProvider",
               "org.ocpsoft.rewrite.servlet.impl.Servlet3DispatcherTypeProvider",
               "org.ocpsoft.rewrite.servlet.impl.Servlet25DispatcherTypeProvider");

      addAsResource(archive, new File("../impl-servlet-2.5/target/classes/org"));
      addAsResource(archive, new File("../impl-servlet-2.5/target/classes/META-INF"));
      addAsResource(archive, new File("../impl-servlet-3.0/target/classes/org"));
      addAsResource(archive, new File("../impl-servlet-3.0/target/classes/META-INF"));

      addAsResource(archive, new File("../impl-servlet/target/classes/org"));
      addAsResource(archive, new File("../impl-servlet/target/classes/META-INF"));

      return archive;
   }

   private static void addAsResource(JavaArchive archive, File file)
   {
      if (file != null && file.exists())
         archive.addAsResource(file);
   }

   protected static JavaArchive getRewriteAnnotationsArchive()
   {
      /*
       * FIXME: There is already a different archive named "rewrite-annotations.jar"
       * which worked fine with Arquillian 1.0.4 but not with 1.1.1. So for now
       * I renamed this archive to "rewrite-annotations2.jar"
       */
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-annotations2.jar");

      addAsResource(archive, new File("../annotations-api/target/classes/org"));
      addAsResource(archive, new File("../annotations-impl/target/classes/org"));
      addAsResource(archive, new File("../annotations-impl/target/classes/META-INF"));

      // if 'config-annotations' is currently tested, don't add it here, because it will be added via
      // getCurrentArchive()
      if (!new File("target/classes").getAbsolutePath().contains("config-annotations")) {
         addAsResource(archive, new File("../config-annotations/target/classes/org"));
         addAsResource(archive, new File("../config-annotations/target/classes/META-INF"));
      }

      return archive.addAsResource(new StringAsset("placeholder"), "README");
   }

   protected static JavaArchive getRewriteCDIArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-integration-cdi.jar");

      addAsResource(archive, new File("../integration-cdi/target/classes/org"));
      addAsResource(archive, new File("../integration-cdi/target/classes/META-INF"));

      return archive;
   }

   protected static JavaArchive getRewriteConfigArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-config-servlet.jar");

      addAsResource(archive, new File("../config-servlet/target/classes/org"));
      addAsResource(archive, new File("../config-servlet/target/classes/META-INF"));

      return archive;
   }

   protected static JavaArchive getRewriteFacesArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-integration-faces.jar");

      addAsResource(archive, new File("../integration-faces/target/classes/org"));
      addAsResource(archive, new File("../integration-faces/target/classes/META-INF"));

      return archive;
   }
}
