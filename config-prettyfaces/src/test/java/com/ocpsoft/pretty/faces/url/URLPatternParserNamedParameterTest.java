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

import java.util.List;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lb3
 */
public class URLPatternParserNamedParameterTest
{
   URLPatternParser parser = new URLPatternParser("/project/#{1:paramsBean.project}/#{two:paramsBean.iteration}/#{3:paramsBean.story}");

   @Test
   public void testUrlPatternParser()
   {
      assertThat(parser).isInstanceOf(URLPatternParser.class);
   }

   @Test
   public void testGetNamedParameters()
   {
      List<PathParameter> params = parser.parse(new URL("/project/starfish1/sprint1/story1"));
      assertThat(params.size()).isEqualTo(3);
      assertThat(params.get(0).getValue()).isEqualTo("starfish1");
      assertThat(params.get(1).getValue()).isEqualTo("sprint1");
      assertThat(params.get(2).getValue()).isEqualTo("story1");

      assertThat(params.get(0).getName()).isEqualTo("1");
      assertThat(params.get(1).getName()).isEqualTo("two");
      assertThat(params.get(2).getName()).isEqualTo("3");

      assertThat(params.get(0).getExpression().getELExpression()).isEqualTo("#{paramsBean.project}");
      assertThat(params.get(1).getExpression().getELExpression()).isEqualTo("#{paramsBean.iteration}");
      assertThat(params.get(2).getExpression().getELExpression()).isEqualTo("#{paramsBean.story}");
   }
}
