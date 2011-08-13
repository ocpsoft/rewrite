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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.ocpsoft.rewrite.servlet.config.parameters.impl.Parameter;
import com.ocpsoft.rewrite.servlet.config.parameters.impl.ParameterizedExpression;
import com.ocpsoft.rewrite.servlet.util.Maps;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class CompiledPathTest
{

   @Test
   public void testMatchesEmptyPath()
   {
      ParameterizedExpression path = new ParameterizedExpression("");

      Assert.assertEquals(0, path.getParameters().size());
      Assert.assertTrue(path.matches(""));
      Map<Parameter, String[]> results = path.parseEncoded("");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesBarePath()
   {
      ParameterizedExpression path = new ParameterizedExpression("/");

      Assert.assertEquals(0, path.getParameters().size());
      Assert.assertTrue(path.matches("/"));

      Map<Parameter, String[]> results = path.parseEncoded("/");
      Assert.assertNotNull(results);
   }

   @Test
   public void testMatchesWithParameters()
   {
      ParameterizedExpression path = new ParameterizedExpression("/{customer}/orders/{id}");

      Map<String, Parameter> parameters = path.getParameters();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<Parameter, String[]> results = path.parseEncoded("/lincoln/orders/24");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test
   public void testMatchesWithParametersAndTrailingSlash()
   {
      ParameterizedExpression path = new ParameterizedExpression("/{customer}/orders/{id}/");

      Map<String, Parameter> parameters = path.getParameters();
      Assert.assertEquals(2, parameters.size());
      Assert.assertEquals("customer", parameters.get("customer").getName());
      Assert.assertEquals("id", parameters.get("id").getName());

      Map<Parameter, String[]> results = path.parseEncoded("/lincoln/orders/24/");
      Assert.assertEquals("lincoln", results.get(path.getParameter("customer"))[0]);
      Assert.assertEquals("24", results.get(path.getParameter("id"))[0]);
   }

   @Test
   public void testBuildEmpty()
   {
      ParameterizedExpression path = new ParameterizedExpression("");
      Assert.assertEquals("", path.build(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildBarePath()
   {
      ParameterizedExpression path = new ParameterizedExpression("/");
      Assert.assertEquals("/", path.build(new LinkedHashMap<String, List<Object>>()));
   }

   @Test
   public void testBuildWithParameters()
   {
      ParameterizedExpression path = new ParameterizedExpression("/{customer}/orders/{id}");
      Map<String, List<Object>> map = new LinkedHashMap<String, List<Object>>();
      Maps.addListValue(map, "customer", "lincoln");
      Maps.addListValue(map, "id", "24");
      Assert.assertEquals("/lincoln/orders/24", path.build(map));
   }

}
