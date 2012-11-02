/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.el;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.el.processor.RegexOverride;

public class RegexOverrideExpressionProcessorTest
{
   RegexOverride p = new RegexOverride();

   @Test
   public void testSpacing1() throws Exception
   {
      String expression = "#{/test/name}";
      PathParameter param = new PathParameter();
      param.setExpression(expression);
      param = p.process(param);

      assertEquals("#{name}", param.getExpression().getELExpression());
      assertEquals("test", param.getRegex());
   }

   @Test
   public void testSpacing2() throws Exception
   {
      String expression = "#{ /test/ name:point}";
      PathParameter param = new PathParameter();
      param.setExpression(expression);
      param = p.process(param);

      assertEquals("#{name:point}", param.getExpression().getELExpression());
      assertEquals("test", param.getRegex());
   }

   @Test
   public void testSpacing3() throws Exception
   {
      String expression = "#{/test/  name}";
      PathParameter param = new PathParameter();
      param.setExpression(expression);
      param = p.process(param);

      assertEquals("#{name}", param.getExpression().getELExpression());
      assertEquals("test", param.getRegex());
   }

   @Test
   public void testSpacing4() throws Exception
   {
      String expression = "#{  /test/name:point.p}";
      PathParameter param = new PathParameter();
      param.setExpression(expression);
      param = p.process(param);

      assertEquals("#{name:point.p}", param.getExpression().getELExpression());
      assertEquals("test", param.getRegex());
   }

   @Test
   public void testBoundaries() throws Exception
   {
      String expression = "#{  /te/st/name}";
      PathParameter param = new PathParameter();
      param.setExpression(expression);
      param = p.process(param);

      assertEquals("#{name}", param.getExpression().getELExpression());
      assertEquals("te/st", param.getRegex());
   }

   @Test
   public void testBoundaries2() throws Exception
   {
      String expression = "#{  /te[^/]+/st/name}";
      PathParameter param = new PathParameter();
      param.setExpression(expression);
      param = p.process(param);

      assertEquals("#{name}", param.getExpression().getELExpression());
      assertEquals("te[^/]+/st", param.getRegex());
   }
}
