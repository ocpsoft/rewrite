/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ocpsoft.rewrite.prettyfaces;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class PrettyFacesTestBase extends RewriteTest
{
   public static WebArchive getDeployment()
   {
      return RewriteTest.getDeploymentWithCDI()
               .setWebXML("web.xml")
               .addAsWebInfResource("faces-config.xml")
               .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
               .addAsLibraries(getPrettyFacesArchive())
               .addAsLibraries(getRewriteFacesArchive())
               .addAsLibraries(getRewriteCDIArchive())
               .addAsLibraries(resolveDependencies("commons-digester:commons-digester:2.0"));
   }

   protected static JavaArchive getPrettyFacesArchive()
   {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "rewrite-config-prettyfaces.jar")

               .addAsResource(new File("../config-prettyfaces/target/classes/org"))
               .addAsResource(new File("../config-prettyfaces/target/classes/com"))
               .addAsResource(new File("../config-prettyfaces/target/classes/META-INF"));

      return archive;
   }
}
