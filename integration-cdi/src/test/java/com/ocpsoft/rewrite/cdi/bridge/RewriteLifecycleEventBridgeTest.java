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
package com.ocpsoft.rewrite.cdi.bridge;

import static org.junit.Assert.fail;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ocpsoft.rewrite.cdi.CDIRoot;
import com.ocpsoft.rewrite.test.RewriteTestBase;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RewriteLifecycleEventBridgeTest extends RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTestBase.getDeployment()
               .addPackages(true, CDIRoot.class.getPackage())
               .addAsResource(
                        new StringAsset(RewriteLifecycleEventBridge.class.getName()),
                        ArchivePaths
                                 .create("META-INF/services/com.ocpsoft.rewrite.servlet.spi.RewriteLifecycleListener"));
      System.out.println(deployment.toString(true));
      return deployment;
   }

   @ArquillianResource
   URL baseURL;

   @Override
   @Test
   public void test()
   {
      makeCall("/page");
      fail("Not yet implemented");
   }

   @Override
   protected void makeCall(String path)
   {
      DefaultHttpClient httpclient = new DefaultHttpClient();
      try
      {
         HttpGet httpget = new HttpGet(baseURL.toExternalForm() + path);

         HttpResponse response = httpclient.execute(httpget);

         HttpEntity entity = response.getEntity();
         if (entity != null)
            entity.consumeContent();

         StatusLine statusLine = response.getStatusLine();
         System.out.println("Status: " + statusLine);
         Assert.assertEquals(200, statusLine.getStatusCode());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
