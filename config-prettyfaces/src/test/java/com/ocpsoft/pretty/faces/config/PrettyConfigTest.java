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

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class PrettyConfigTest
{
   private static PrettyConfig config = new PrettyConfig();
   private static UrlMapping mapping = new UrlMapping();
   private static UrlMapping mapping1 = new UrlMapping();
   private static UrlMapping mapping2 = new UrlMapping();

   @BeforeClass
   public static void setUpBeforeClass() throws Exception
   {
      List<UrlMapping> mappings = new ArrayList<UrlMapping>();
      mapping.setId("testid");
      mapping.setPattern("/home/en/#{testBean.someProperty}/");
      mapping.setViewId("/faces/view.jsf");
      mappings.add(mapping);

      mapping1.setId("testid2");
      mapping1.setPattern("/home/en/#{testBean.someProperty2}/");
      mapping1.setViewId("/faces/view.jsf");
      mappings.add(mapping1);

      mapping2.setId("testid2");
      mapping2.setPattern("/home/en/#{testBean.someProperty2}/");
      mapping2.setViewId("/faces/view.jsf");
      mappings.add(mapping2);

      config.setMappings(mappings);
   }

   @Test
   public void testGetMappingById()
   {
      UrlMapping mapping2 = config.getMappingById("testid");
      assertThat(mapping2).isEqualTo(mapping);
   }

   @Test
   public void testGetMappingByNullIdReturnsNull()
   {
      UrlMapping mapping2 = config.getMappingById(null);
      assertThat(mapping2).isEqualTo(null);
   }

   @Test
   public void testGetMappingForUrl()
   {
      UrlMapping mapping2 = config.getMappingForUrl(new URL("/home/en/test/"));
      assertThat(mapping2).isEqualTo(mapping);
   }

   @Test
   public void isViewMapped() throws Exception
   {
      assertThat(config.isViewMapped("/faces/view.jsf")).isTrue();
      assertThat(config.isViewMapped("/faces/view2.jsf")).isFalse();
   }

   @Test
   public void isNullViewMappedReturnsFalse() throws Exception
   {
      assertThat(config.isViewMapped(null)).isFalse();
   }

   @Test
   public void testIsURLMapped() throws Exception
   {
      assertThat(config.isURLMapped(new URL("/home/en/test/"))).isTrue();
      assertThat(config.isViewMapped("/home/en/notmapped/okthen")).isFalse();
   }

}
