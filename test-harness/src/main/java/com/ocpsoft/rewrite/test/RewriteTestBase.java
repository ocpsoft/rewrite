package com.ocpsoft.rewrite.test;

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

import static org.junit.Assert.fail;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class RewriteTestBase
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      return ShrinkWrap
               .create(WebArchive.class, "rewrite-test.war")
               // .addAsLibraries(new File("../api/target/classes"),
               // new File("../impl-servlet/target/classes"))
               .addAsWebResource(
                        new StringAsset("<beans/>"),
                        ArchivePaths.create("WEB-INF/beans.xml"))
               .addAsWebResource("jetty-env.xml",
                        ArchivePaths.create("WEB-INF/jetty-env.xml"));
   }

   @ArquillianResource
   URL baseURL;

   @Test
   public void test()
   {
      makeCall("/page");
      fail("Not yet implemented");
   }

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
