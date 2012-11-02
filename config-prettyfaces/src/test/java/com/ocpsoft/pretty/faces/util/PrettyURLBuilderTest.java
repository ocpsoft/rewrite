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
package com.ocpsoft.pretty.faces.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIParameter;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.component.Link;
import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;

/**
 * @author lb3
 */
public class PrettyURLBuilderTest
{
   static Link link = new Link();
   static UrlMapping mapping = new UrlMapping();
   static UIParameter param1 = new UIParameter();
   static UIParameter param2 = new UIParameter();
   static UIParameter param3 = new UIParameter();
   static UIParameter param4 = new UIParameter();
   static UIParameter param5 = new UIParameter();
   static String expectedPath = "";

   static List<Object> values = new ArrayList<Object>();
   static Object[] valuesArray;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception
   {
      mapping.addAction(new UrlAction("#{bean.action}"));
      mapping.setId("testMapping");
      mapping.setPattern("/test/#{bean.param1}/mapping/#{bean.param2}");
      mapping.addQueryParam(new QueryParameter("key1", "#{bean.qp1}"));
      mapping.addQueryParam(new QueryParameter("key2", "#{bean.qp2}"));

      param1.setName("key1");
      param1.setValue("qp1");
      link.getChildren().add(param1);

      param2.setValue("up1");
      link.getChildren().add(param2);
      values.add(param2.getValue());

      param3.setName("key2");
      param3.setValue("qp2");
      link.getChildren().add(param3);

      param4.setValue("up2");
      link.getChildren().add(param4);
      values.add(param4.getValue());
      valuesArray = values.toArray();

      param5.setName("double");
      param5.setValue(new Object[] { "12", "34" });
      link.getChildren().add(param5);

      expectedPath = "/test/" + param2.getValue() + "/mapping/" + param4.getValue();

      link.getAttributes().put("mappingId", mapping.getId());
   }

   private final PrettyURLBuilder builder = new PrettyURLBuilder();

   @Test
   public void testExtractParameters()
   {
      List<UIParameter> parameters = builder.extractParameters(link);
      assertEquals(5, parameters.size());
      assertTrue(parameters.contains(param1));
      assertTrue(parameters.contains(param2));
      assertTrue(parameters.contains(param3));
      assertTrue(parameters.contains(param4));
      assertTrue(parameters.contains(param5));
   }

   @Test
   public void testBuildMappedUrlPrettyUrlMappingListOfUIParameter()
   {
      List<UIParameter> parameters = builder.extractParameters(link);
      String mappedUrl = builder.build(mapping, false, parameters);
      assertTrue(mappedUrl.startsWith(expectedPath));
      assertTrue(mappedUrl.contains(param1.getName() + "=" + param1.getValue()));
      assertTrue(mappedUrl.contains(param3.getName() + "=" + param3.getValue()));
   }

   @Test
   public void testBuildMappedUrlPrettyUrlMappingListOfUIParameterContainsArrayQueryParam()
   {
      List<UIParameter> parameters = builder.extractParameters(link);
      String mappedUrl = builder.build(mapping, false, parameters);
      assertTrue(mappedUrl.startsWith(expectedPath));
      assertTrue(mappedUrl.contains(param5.getName() + "=" + ((Object[]) param5.getValue())[0]));
      assertTrue(mappedUrl.contains(param5.getName() + "=" + ((Object[]) param5.getValue())[1]));
   }

   @Test
   public void testBuildMappedUrlPrettyUrlMappingSingleParameterContainingList()
   {
      List<UIParameter> parameters = new ArrayList<UIParameter>();
      UIParameter param = new UIParameter();
      param.setValue(values);
      parameters.add(param);

      String mappedUrl = builder.build(mapping, false, parameters);
      assertTrue(mappedUrl.startsWith(expectedPath));
   }

   @Test
   public void testBuildMappedUrlPrettyUrlMappingSingleParameterContainingArray()
   {
      List<UIParameter> parameters = new ArrayList<UIParameter>();
      UIParameter param = new UIParameter();
      param.setValue(valuesArray);
      parameters.add(param);

      String mappedUrl = builder.build(mapping, false, parameters);
      assertTrue(mappedUrl.startsWith(expectedPath));
   }

   @Test(expected = PrettyException.class)
   public void testBuildMappedUrlPrettyUrlMappingSingleNamedParameterDefaultsToNonListBuild()
   {
      List<UIParameter> parameters = new ArrayList<UIParameter>();
      UIParameter param = new UIParameter();
      param.setValue(values);
      param.setName("something");
      parameters.add(param);

      builder.build(mapping, false, parameters);
   }
   
   @Test
   public void testBuildUrlWithUnicodeCharacters()
   {
      List<UIParameter> params = Arrays.asList(
            createUIParameter(null, "\u20ac"),    // Euro sign
            createUIParameter(null, "\u0142"),    // L with stroke
            createUIParameter("key1", "\u00a3"),  // pound sign
            createUIParameter("key2", "\u0644")   // Lamedh
      );
      
      // encoded
      String encodedUrl = builder.build(mapping, true, params);
      assertTrue(encodedUrl.startsWith("/test/%E2%82%AC/mapping/%C5%82?"));
      assertTrue(encodedUrl.contains("key1=%C2%A3"));
      assertTrue(encodedUrl.contains("key2=%D9%84"));

      // unicode
      String unicodeUrl = builder.build(mapping, false, params);
      assertTrue(unicodeUrl.startsWith("/test/\u20ac/mapping/\u0142?"));
      assertTrue(unicodeUrl.contains("key1=%C2%A3"));
      assertTrue(unicodeUrl.contains("key2=%D9%84"));

      // unicode
      String oldUrl = builder.build(mapping, false, params);
      assertTrue(oldUrl.startsWith("/test/\u20ac/mapping/\u0142?"));
      assertTrue(oldUrl.contains("key1=%C2%A3"));
      assertTrue(oldUrl.contains("key2=%D9%84"));

   }
   
   private final static UIParameter createUIParameter(String name, Object value) {
      UIParameter p = new UIParameter();
      p.setName(name);
      p.setValue(value);
      return p;
   }

}
