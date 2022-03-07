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
package org.ocpsoft.rewrite.servlet.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * FIXME:  Remove references to deprecated code.
 * Leaving test class while the code is still usable.  
 * Adding task as a better method of tracking.  
 */
@Deprecated
public class QueryStringBuilderTest
{
   @Test
   public void testCreateEmptyQueryStringBuilder()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();

      QueryStringBuilder qs = QueryStringBuilder.createFromArrays(params);
      String queryString = qs.toQueryString();
      assertThat(queryString).isEqualTo("");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateStringQueryStringBuilder() throws Exception
   {
      String query = "?bar=555&foo=hello&foo=friend";
      QueryStringBuilder qs = QueryStringBuilder.createFromEncoded(query);

      String result = qs.toQueryString();

      assertThat(result).startsWith("?");
      assertThat(result).contains("bar=555");
      assertThat(result).contains("foo=hello");
      assertThat(result).contains("foo=friend");
   }

   @Test
   public void testCreateStringQueryStringBuilderWithExtraPreCharacters() throws Exception
   {
      String query = "?bar=555&foo=hello&foo=friend";
      QueryStringBuilder qs = QueryStringBuilder.createFromEncoded("www.ocpsoft.com/" + query);

      String result = qs.toQueryString();

      assertThat(result).startsWith("?");
      assertThat(result).contains("bar=555");
      assertThat(result).contains("foo=hello");
      assertThat(result).contains("foo=friend");
   }

   @Test
   public void testCreateFromQuestionMarkYieldsEmptyQueryStringBuilder() throws Exception
   {
      String query = "?";
      QueryStringBuilder qs = QueryStringBuilder.createFromEncoded(query);

      String result = qs.toQueryString();

      assertThat(result.length() == 0).isTrue();
   }

   @Test
   public void testCreateFromQuestionMarks() throws Exception
   {
      String query = "????";
      QueryStringBuilder qs = QueryStringBuilder.createFromEncoded(query);

      String result = qs.toQueryString();

      assertThat(new ArrayList<String>(qs.getParameterNames()).get(0)).isEqualTo("???");
      assertThat(result).isEqualTo("????");
   }

   @Test
   public void testCreateFromAmpersands() throws Exception
   {
      String query = "?%26%26%26";
      QueryStringBuilder qs = QueryStringBuilder.createFromEncoded(query);

      String result = qs.decode().toQueryString();

      assertThat(new ArrayList<String>(qs.getParameterNames()).get(0)).isEqualTo("%26%26%26");
      assertThat(result).isEqualTo("?&&&");
   }

   @Test
   public void testCreateQueryStringBuilder()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "val1" });

      QueryStringBuilder qs = QueryStringBuilder.createFromArrays(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1=val1");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateUnValuedQueryString()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", null);

      QueryStringBuilder qs = QueryStringBuilder.createFromArrays(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateBlankValuedQueryStringBuilder()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "" });

      QueryStringBuilder qs = QueryStringBuilder.createFromArrays(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1=");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateMultiValueQueryStringBuilder()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "val1", "val2" });

      QueryStringBuilder qs = QueryStringBuilder.createFromArrays(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).isEqualTo("?p1=val1&p1=val2");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateMultiKeyedMultiValueQueryStringBuilder()
   {
      Map<String, String[]> params = new TreeMap<String, String[]>();
      params.put("p1", new String[] { "val1", "val2" });
      params.put("p2", new String[] { "val3", "val4" });

      QueryStringBuilder qs = QueryStringBuilder.createFromArrays(params);
      String queryString = qs.toQueryString();

      assertThat(queryString).startsWith("?");
      assertThat(queryString).contains("p1=val1");
      assertThat(queryString).contains("p1=val2");
      assertThat(queryString).contains("p2=val3");
      assertThat(queryString).contains("p2=val4");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testCreateMultiKeyedMultiValueQueryStringBuilderList()
   {
      Map<String, List<String>> paramMap = new LinkedHashMap<String, List<String>>();
      List<String> params = new ArrayList<String>();
      params.add("val1");
      params.add("val2");

      paramMap.put("p1", params);

      params = new ArrayList<String>();
      params.add("val3");
      params.add("val4");

      paramMap.put("p2", params);

      QueryStringBuilder qs = QueryStringBuilder.createNew().addParameterLists(paramMap);
      String queryString = qs.toQueryString();

      assertThat(queryString).startsWith("?");
      assertThat(queryString).contains("p1=val1");
      assertThat(queryString).contains("p1=val2");
      assertThat(queryString).contains("p2=val3");
      assertThat(queryString).contains("p2=val4");

      qs = QueryStringBuilder.createFromEncoded(queryString);
      assertThat(qs.toQueryString()).isEqualTo(queryString);
   }

   @Test
   public void testAddParametersOneSimpleParameter()
   {
      QueryStringBuilder qs = new QueryStringBuilder();
      qs.addParameters("a=b");
      assertThat(qs.toQueryString()).isEqualTo("?a=b");
   }

   @Test
   public void testAddParametersMultipleParametersEncoded()
   {
      QueryStringBuilder qs = new QueryStringBuilder();
      qs.addParameters("a=b+c&d=e");
      assertThat(qs.toQueryString()).isEqualTo("?a=b+c&d=e");
   }

   @Test
   public void testAddParametersEncodedAmpersand()
   {
      // http://code.google.com/p/prettyfaces/issues/detail?id=104
      QueryStringBuilder qs = new QueryStringBuilder();
      qs.addParameters("a=b&amp;c=d");
      assertThat(qs.toQueryString()).isEqualTo("?a=b&c=d");
   }

   @Test
   public void testDecodeAmpersand()
   {
      QueryStringBuilder qs = new QueryStringBuilder();
      qs.addParameter("a", "b%26c=d");
      assertThat(qs.decode().toQueryString()).isEqualTo("?a=b&c=d");
   }

   @Test
   public void testEncodeAmpersand()
   {
      QueryStringBuilder qs = new QueryStringBuilder();
      qs.addParameter("a", "b&c=d");
      assertThat(qs.encode().toQueryString()).isEqualTo("?a=b%26c%3Dd");
   }

}
