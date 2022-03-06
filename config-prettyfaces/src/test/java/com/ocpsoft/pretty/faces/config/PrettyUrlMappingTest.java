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
package com.ocpsoft.pretty.faces.config;

import java.util.List;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.url.URLPatternParser;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lb3
 */
public class PrettyUrlMappingTest
{
   UrlMapping m = new UrlMapping();

   public PrettyUrlMappingTest()
   {
      m.setId("foo");
      m.setPattern("/project/#{pb.name}");
      m.addPathValidator(new PathValidator(0, "val1 val2 val3", "#{handler.handle}"));
      m.addPathValidator(new PathValidator(2, "val4 val5", "#{handler.handle}"));
   }

   @Test
   public void testGetPatternParser()
   {
      URLPatternParser parser = m.getPatternParser();
      assertThat(parser.getParameterCount()).isEqualTo(1);
   }

   @Test
   public void testGetValidatorsIdsForPathParam()
   {

      PathParameter p = new PathParameter();
      p.setPosition(0);

      List<PathValidator> validators = m.getValidatorsForPathParam(p);
      assertThat(validators.size()).isEqualTo(1);

      PathParameter p2 = new PathParameter();
      p2.setPosition(2);

      validators = m.getValidatorsForPathParam(p2);
      assertThat(validators.size()).isEqualTo(1);
   }

   @Test
   public void testMatches()
   {
      assertThat(m.matches(new URL("/project/scrumshark"))).isTrue();
      assertThat(m.matches(new URL("/project/foo/bar"))).isFalse();
   }

}
