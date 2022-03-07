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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.RequestParameter;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryStringTest
{
   @Test
   public void testCreateEmptyQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();
      assertThat(queryString).isEqualTo("");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateStringQueryString() throws Exception
   {
      String query = "?bar=555&foo=hello&foo=friend";
      QueryString qs = QueryString.build(query);

      String result = qs.toQueryString();

      assertThat(result.startsWith("?")).isTrue();
      assertThat(result).contains("bar=555");
      assertThat(result).contains("foo=hello");
      assertThat(result).contains("foo=friend");
   }

   @Test
   public void testCreateStringQueryStringWithExtraPreCharacters() throws Exception
   {
      String query = "?bar=555&foo=hello&foo=friend";
      QueryString qs = QueryString.build("www.ocpsoft.com/" + query);

      String result = qs.toQueryString();

      assertThat(result.startsWith("?")).isTrue();
      assertThat(result).contains("bar=555");
      assertThat(result).contains("foo=hello");
      assertThat(result).contains("foo=friend");
   }

   @Test
   public void testCreateFromQuestionMarkYieldsEmptyQueryString() throws Exception
   {
      String query = "?";
      QueryString qs = QueryString.build(query);

      String result = qs.toQueryString();

      assertThat(result.length() == 0).isTrue();
   }

   @Test
   public void testCreateQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "val1" });

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1=val1");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateUnValuedQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", null);

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateBlankValuedQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "" });

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1=");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateMultiValueQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "val1", "val2" });

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1=val1&p1=val2");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateMultiKeyedMultiValueQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "val1", "val2" });
      params.put("p2", new String[] { "val3", "val4" });

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();

      assertThat(queryString.startsWith("?")).isTrue();
      assertThat(queryString).contains("p1=val1");
      assertThat(queryString).contains("p1=val2");
      assertThat(queryString).contains("p2=val3");
      assertThat(queryString).contains("p2=val4");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateMultiKeyedMultiValueQueryStringList()
   {
      List<RequestParameter> params = new ArrayList<RequestParameter>();
      params.add(new QueryParameter("p1", "val1"));
      params.add(new QueryParameter("p1", "val2"));
      params.add(new QueryParameter("p2", "val3"));
      params.add(new QueryParameter("p2", "val4"));

      QueryString qs = QueryString.build(params);
      String queryString = qs.toQueryString();

      assertThat(queryString.startsWith("?")).isTrue();
      assertThat(queryString).contains("p1=val1");
      assertThat(queryString).contains("p1=val2");
      assertThat(queryString).contains("p2=val3");
      assertThat(queryString).contains("p2=val4");

      qs = QueryString.build(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }
   
   @Test
   public void testAddParametersOneSimpleParameter()
   {
      QueryString qs = new QueryString();
      qs.addParameters("a=b");
      assertThat(qs.toQueryString()).isEqualTo("?a=b");
   }

   @Test
   public void testAddParametersMultipleParametersEncoded()
   {
      QueryString qs = new QueryString();
      qs.addParameters("a=b+c&d=e");
      assertThat(qs.toQueryString()).isEqualTo("?a=b+c&d=e");
   }

   @Test
   public void testAddParametersEncodedAmpersand()
   {
      // http://code.google.com/p/prettyfaces/issues/detail?id=104
      QueryString qs = new QueryString();
      qs.addParameters("a=b&amp;c=d");
      assertThat(qs.toQueryString()).isEqualTo("?a=b&c=d");
   }

}
