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
package com.ocpsoft.pretty.faces.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

/**
 * @author lb3
 */
public class URLPatternParserNamedParameterTest
{
   URLPatternParser parser = new URLPatternParser("/project/#{1:paramsBean.project}/#{two:paramsBean.iteration}/#{3:paramsBean.story}");

   @Test
   public void testUrlPatternParser()
   {
      assertTrue(parser instanceof URLPatternParser);
   }

   @Test
   public void testGetNamedParameters()
   {
      List<PathParameter> params = parser.parse(new URL("/project/starfish1/sprint1/story1"));
      assertEquals(3, params.size());
      assertEquals("starfish1", params.get(0).getValue());
      assertEquals("sprint1", params.get(1).getValue());
      assertEquals("story1", params.get(2).getValue());

      assertEquals("1", params.get(0).getName());
      assertEquals("two", params.get(1).getName());
      assertEquals("3", params.get(2).getName());

      assertEquals("#{paramsBean.project}", params.get(0).getExpression().getELExpression());
      assertEquals("#{paramsBean.iteration}", params.get(1).getExpression().getELExpression());
      assertEquals("#{paramsBean.story}", params.get(2).getExpression().getELExpression());
   }
}
