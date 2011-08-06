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
package com.ocpsoft.rewrite.servlet.config.parameters;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CompiledPathTest
{

   @Test
   public void testMatchesBarePath()
   {
      CompiledPath path = new CompiledPath(null, "/");

      Map<String, PathParameter> parameters = path.getParameters();
      Assert.assertEquals(0, parameters.size());

      Assert.assertTrue(path.matches("/"));
      Map<PathParameter, String> results = path.parseEncoded("/");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesWithParameters()
   {
      CompiledPath path = new CompiledPath(null, "/{customer}/orders/{id}");

      Map<String, PathParameter> parameters = path.getParameters();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<PathParameter, String> results = path.parseEncoded("/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer")));
      Assert.assertEquals("24", results.get(path.getParameter("id")));
   }

}
