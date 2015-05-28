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
package org.ocpsoft.rewrite.annotation.visit;

import org.junit.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.annotation.config.AnnotationConfigProvider;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AnnotationConfigurationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, AnnotationConfigurationTest.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, AnnotationConfigProvider.class)
               .addAsServiceProvider(AnnotationHandler.class, FieldHandler.class, MethodHandler.class,
                        ParamHandler.class, TypeHandler.class);
      return deployment;
   }

   @Test
   public void testControl() throws Exception
   {
      HttpAction<HttpGet> action = get("/annotation/control");
      Assert.assertEquals(404, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testTypeAnnotation() throws Exception
   {
      HttpAction<HttpGet> action = get("/annotation/type");
      Assert.assertEquals(204, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testFieldAnnotation() throws Exception
   {
      HttpAction<HttpGet> action = get("/annotation/field");
      Assert.assertEquals(201, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testMethodAnnotation() throws Exception
   {
      HttpAction<HttpGet> action = get("/annotation/method");
      Assert.assertEquals(202, action.getResponse().getStatusLine().getStatusCode());
   }

   @Test
   public void testParameterAnnotation() throws Exception
   {
      HttpAction<HttpGet> action = get("/annotation/parameter");
      Assert.assertEquals(203, action.getResponse().getStatusLine().getStatusCode());
   }
}
