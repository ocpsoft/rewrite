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

package com.ocpsoft.rewrite.servlet.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.exception.RewriteException;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class QueryStringBuilder
{
   private final static Logger log = Logger.getLogger(QueryStringBuilder.class);

   private final Map<String, List<String>> parameters = new LinkedHashMap<String, List<String>>();

   /**
    * Return a new empty instance of {@link QueryStringBuilder}
    */
   public static QueryStringBuilder begin()
   {
      return new QueryStringBuilder();
   }

   /**
    * Build a query string from the given URL. If a '?' character is encountered in the URL, the any characters up to
    * and including the first '?' will be ignored.
    */
   public static QueryStringBuilder build(final String url)
   {
      QueryStringBuilder queryString = new QueryStringBuilder();
      queryString.addParameters(url);
      return queryString;
   }

   /**
    * Build a query string from the given map of name=value pairs. For parameters with more than one value, each value
    * will be appended using the same name.
    */
   public static QueryStringBuilder buildFromArrays(final Map<String, String[]> params)
   {
      return QueryStringBuilder.begin().addParameterArrays(params);
   }

   /**
    * Build a query string from the given map of name=value pairs. For parameters with more than one value, each value
    * will be appended using the same name.
    */
   public static QueryStringBuilder buildFromLists(final Map<String, List<String>> params)
   {
      return QueryStringBuilder.begin().addParameterLists(params);
   }

   /**
    * Add parameters from the given URL. If a '?' character is encountered in the URL, the any characters up to and
    * including the first '?' will be ignored. If a parameter already exists, append new values to the existing list of
    * values for that parameter.
    */
   public QueryStringBuilder addParameters(String url)
   {
      if ((url != null) && !"".equals(url))
      {
         url = url.trim();
         if (url.length() > 1)
         {
            if (url.contains("?"))
            {
               url = url.substring(url.indexOf('?') + 1);
            }

            String pairs[] = url.split("&(amp;)?");
            for (String pair : pairs)
            {
               String name;
               String value;
               int pos = pair.indexOf('=');
               // for "n=", the value is "", for "n", the value is null
               if (pos == -1)
               {
                  name = pair;
                  value = null;
               }
               else
               {
                  name = pair.substring(0, pos);
                  value = pair.substring(pos + 1, pair.length());
               }
               List<String> list = parameters.get(name);
               if (list == null)
               {
                  list = new ArrayList<String>();
                  parameters.put(name, list);
               }
               list.add(value);
            }
         }
      }
      return this;
   }

   public void addParameter(final String name, final String... values)
   {
      Map<String, String[]> parameter = new LinkedHashMap<String, String[]>();
      parameter.put(name, values);
      addParameterArrays(parameter);
   }

   /**
    * Add parameters from the given map of name=value pairs. For parameters with more than one value, each value will be
    * appended using the same name. If a parameter already exists, append new values to the existing list of values for
    * that parameter.
    */
   public QueryStringBuilder addParameterArrays(final Map<String, String[]> params)
   {
      if (params != null)
      {
         for (Entry<String, String[]> entry : params.entrySet())
         {
            List<String> values = null;
            if (entry.getValue() != null)
            {
               values = Arrays.asList(entry.getValue());
            }
            parameters.put(entry.getKey(), values);
         }
      }
      return this;
   }

   /**
    * Add parameters from the given map of name=value pairs. For parameters with more than one value, each value will be
    * appended using the same name. If a parameter already exists, append new values to the existing list of values for
    * that parameter.
    * 
    * @return
    */
   public QueryStringBuilder addParameterLists(final Map<String, List<String>> params)
   {
      if (params != null)
      {
         for (Entry<String, List<String>> entry : params.entrySet())
         {
            List<String> values = null;
            if (entry.getValue() != null)
            {
               values = new ArrayList<String>(entry.getValue());
            }
            parameters.put(entry.getKey(), values);
         }
      }
      return this;
   }

   /**
    * Return a new {@link QueryStringBuilder} instance having called {@link URLDecoder#decode(String, String)} on each
    * name=value pair.
    */
   public QueryStringBuilder decode()
   {
      return new QueryStringBuilder().addParameterLists(getParameterMap(new QSDecoder()));
   }

   /**
    * Return a new {@link QueryStringBuilder} instance having called {@link URLEncoder#encode(String, String)} on each
    * name=value pair.
    */
   public QueryStringBuilder encode()
   {
      return new QueryStringBuilder().addParameterLists(getParameterMap(new QSEncoder()));
   }

   /**
    * Get the first value of given parameter name.
    * 
    * @return The value of the parameter, null if the parameter does not exist, or "" if the parameter exists but has no
    *         values.
    */
   public String getParameter(final String name)
   {
      List<String> values = parameters.get(name);
      if (values == null)
      {
         return null;
      }

      if (values.size() == 0)
      {
         return "";
      }

      return values.get(0);
   }

   /**
    * Get the name, values[] map representing this query string.
    */
   public Map<String, List<String>> getParameterMap()
   {
      return getParameterMap(new NullEncodingHandler());
   }

   private Map<String, List<String>> getParameterMap(final EncodingHandler handler)
   {
      Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
      for (Map.Entry<String, List<String>> entry : parameters.entrySet())
      {
         String key = handler.encode(entry.getKey());
         List<String> values = new ArrayList<String>();
         for (String value : entry.getValue()) {
            if (value != null) {
               values.add(handler.encode(value));
            }
         }
         map.put(key, new ArrayList<String>(values));
      }
      return map;
   }

   /**
    * Get set of parameter names currently stored in this query string.
    */
   public Set<String> getParameterNames()
   {
      return new LinkedHashSet<String>(parameters.keySet());
   }

   /**
    * Get the array of values for a given parameter name.
    * 
    * @return The values of the parameter, null if the parameter does not exist.
    */
   public String[] getParameterValues(final String name)
   {
      List<String> values = parameters.get(name);
      if (values == null)
      {
         return null;
      }

      return values.toArray(new String[values.size()]);
   }

   /*
    * Utility Methods
    */
   /**
    * Return true if this query string currently contains no parameters.
    */
   public boolean isEmpty()
   {
      return parameters.isEmpty();
   }

   public List<String> removeParameter(final String string)
   {
      return parameters.remove(string);
   }

   /**
    * Convert the current parameters to a valid query string, including the leading '?' character.
    * <p>
    * 
    * For example, a {@link QueryStringBuilder} with the values [key=>value, name=>value1,value2,value3] will become:
    * 
    * <pre>
    * 
    * ?key=value&name=value1&name=value2&name=value3
    * </pre>
    * 
    * @return If parameters exist, return a valid query string with leading '?' character. If no parameters exist,
    *         return an empty string.
    */
   public String toQueryString()
   {
      StringBuffer result = new StringBuffer();

      if ((null != parameters) && !parameters.isEmpty())
      {
         result.append("?");
         Iterator<Entry<String, List<String>>> iterator = parameters.entrySet().iterator();
         while (iterator.hasNext())
         {
            Entry<String, List<String>> entry = iterator.next();
            String key = entry.getKey();
            List<String> values = entry.getValue();

            if ((key != null) && !"".equals(key))
            {
               result.append(key);

               if ((values != null) && !values.isEmpty())
               {
                  for (int i = 0; i < values.size(); i++)
                  {
                     String value = values.get(i);
                     if ((value != null) && !"".equals(value))
                     {
                        result.append("=" + value);
                     }
                     else if ((value != null) && "".equals(value))
                     {
                        result.append("=");
                     }

                     if (i < (values.size() - 1))
                     {
                        result.append("&" + key);
                     }
                  }
               }
            }

            if (iterator.hasNext())
            {
               result.append("&");
            }
         }
      }

      return result.toString();
   }

   @Override
   public String toString()
   {
      return toQueryString();
   }

   private interface EncodingHandler
   {
      public String encode(String value);
   }

   private class NullEncodingHandler implements EncodingHandler
   {
      @Override
      public String encode(final String value)
      {
         return value;
      }
   }

   private class QSDecoder implements EncodingHandler
   {
      @Override
      public String encode(final String value)
      {
         try {
            return URLDecoder.decode(value, "UTF-8");
         }
         catch (UnsupportedEncodingException e) {
            throw new RewriteException(e);
         }
         catch (IllegalArgumentException e)
         {
            log.warn("Could not decode query parameter: " + value);
            return value;
         }
      }
   }

   private class QSEncoder implements EncodingHandler
   {
      @Override
      public String encode(final String value)
      {
         try {
            return URLEncoder.encode(value, "UTF-8");
         }
         catch (UnsupportedEncodingException e) {
            throw new RewriteException(e);
         }
         catch (IllegalArgumentException e)
         {
            log.warn("Could not encode query parameter: " + value);
            return value;
         }
      }
   }
}