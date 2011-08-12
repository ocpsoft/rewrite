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

import com.ocpsoft.rewrite.exception.RewriteException;
import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.logging.LoggerFactory;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class QueryStringBuilder
{

   private final static Logger log = LoggerFactory.getLogger(QueryStringBuilder.class);

   private final Map<String, List<String>> parameters = new LinkedHashMap<String, List<String>>();

   /**
    * Build a query string from the given map of name=value pairs. For parameters with more than one value, each value
    * will be appended using the same name.
    */
   public static QueryStringBuilder build(final Map<String, String[]> params)
   {
      QueryStringBuilder queryString = new QueryStringBuilder();
      queryString.addParameters(params);
      return queryString;
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

   /*
    * Work methods
    */
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

   /**
    * Get set of parameter names currently stored in this query string.
    */
   public Set<String> getParameterNames()
   {
      return new LinkedHashSet<String>(parameters.keySet());
   }

   /**
    * Get the name, values[] map representing this query string.
    */
   public Map<String, List<String>> getParameterMap()
   {
      Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
      for (Map.Entry<String, List<String>> entry : parameters.entrySet())
      {
         map.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
      }
      return map;
   }

   /*
    * Add Parameters
    */

   /**
    * Add parameters from the given map of name=value pairs. For parameters with more than one value, each value will be
    * appended using the same name. If a parameter already exists, append new values to the existing list of values for
    * that parameter.
    */
   public void addParameters(final Map<String, String[]> params)
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
   }

   /**
    * Add parameters from the given URL. If a '?' character is encountered in the URL, the any characters up to and
    * including the first '?' will be ignored. If a parameter already exists, append new values to the existing list of
    * values for that parameter.
    */
   public void addParameters(String url)
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
                  try
                  {
                     // FIXME This probably shouldn't be happening here.
                     name = URLDecoder.decode(pair.substring(0, pos), "UTF-8");
                     value = URLDecoder.decode(pair.substring(pos + 1, pair.length()), "UTF-8");
                  }
                  catch (IllegalArgumentException e)
                  {
                     // thrown by URLDecoder if character decoding fails
                     log.warn("Ignoring invalid query parameter: " + pair);
                     continue;
                  }
                  catch (UnsupportedEncodingException e)
                  {
                     throw new RewriteException(
                              "UTF-8 encoding not supported. Something is seriously wrong with your environment.");
                  }
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
      return;
   }

   /**
    * Convert the current parameters to a valid query string, including the leading '?' character. This method
    * automatically URLEncodes query-parameter values
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
      try
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
                  key = URLEncoder.encode(key, "UTF-8");
                  result.append(key);

                  if ((values != null) && !values.isEmpty())
                  {
                     for (int i = 0; i < values.size(); i++)
                     {
                        String value = values.get(i);
                        if ((value != null) && !"".equals(value))
                        {
                           value = URLEncoder.encode(value, "UTF-8");
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
      catch (UnsupportedEncodingException e)
      {
         throw new RewriteException("Error building query string.", e);
      }
   }

   @Override
   public String toString()
   {
      return toQueryString();
   }

   public List<String> removeParameter(final String string)
   {
      return parameters.remove(string);
   }
}