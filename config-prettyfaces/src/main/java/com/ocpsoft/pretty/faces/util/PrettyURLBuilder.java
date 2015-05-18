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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;

import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.RequestParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.QueryString;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.url.URLPatternParser;

/**
 * A utility class for building Pretty URLs.
 * 
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PrettyURLBuilder
{
   /**
    * Extract any {@link UIParameter} objects from a given component. These
    * parameters are what PrettyFaces uses to communicate with the JSF component
    * tree
    * 
    * @param component
    * @return A list of {@link UIParameter} objects, or an empty list if no
    *         {@link UIParameter}s were contained within the component children.
    */
   public List<UIParameter> extractParameters(final UIComponent component)
   {
      List<UIParameter> results = new ArrayList<UIParameter>();
      for (UIComponent child : component.getChildren())
      {
         if (child instanceof UIParameter)
         {
            final UIParameter param = (UIParameter) child;
            if (!param.isDisable())
            {
               results.add(param);
            }
         }
      }
      return results;
   }

   /**
    * Build a Pretty URL for the given UrlMapping and parameters.
    * 
    * @deprecated Use {@link #build(UrlMapping, boolean, Map)} instead.
    */
   @Deprecated
   public String build(final UrlMapping mapping, final Map<String, String[]> parameters)
   {
      return build(mapping, false, parameters);
   }

   
   /**
    * Build a Pretty URL for the given UrlMapping and parameters.
    * 
    * @since 3.2.0
    */
   public String build(final UrlMapping mapping, final boolean encodeUrl, final Map<String, String[]> parameters)
   {
      List<UIParameter> list = new ArrayList<UIParameter>();
      if(parameters != null)
      {
         for (Entry<String, String[]> e : parameters.entrySet())
         {
            UIParameter p = new UIParameter();
            p.setName(e.getKey());
            p.setValue(e.getValue());
            list.add(p);
         }
      }
      return build(mapping, false, list);
   }

   /**
    * Build a Pretty URL for the given UrlMapping and parameters.
    * 
    * @deprecated Use {@link #build(UrlMapping, boolean, Object...)} instead.
    */
   @Deprecated
   public String build(final UrlMapping mapping, final Object... parameters)
   {
      return build(mapping, false, parameters);
   }
   
   
   /**
    * Build a Pretty URL for the given UrlMapping and parameters.
    * 
    * @since 3.2.0 
    */
   public String build(final UrlMapping mapping, final boolean encodeUrl, final Object... parameters)
   {
      List<UIParameter> list = new ArrayList<UIParameter>();
      if(parameters != null)
      {
         for (Object e : parameters)
         {
            UIParameter p = new UIParameter();
            if (e != null)
            {
               p.setValue(e.toString());
            }
            list.add(p);
         }
      }
      return build(mapping, encodeUrl, list);
   }

   /**
    * Build a Pretty URL for the given UrlMapping and parameters.
    * 
    * @deprecated Use {@link #build(UrlMapping, boolean, RequestParameter...)} instead.
    */
   @Deprecated
   public String build(final UrlMapping mapping, final RequestParameter... parameters)
   {
      return build(mapping, false, parameters);
   }

   
   /**
    * Build a Pretty URL for the given UrlMapping and parameters.
    * 
    * @since 3.2.0 
    */
   public String build(final UrlMapping mapping, final boolean encodeUrl, final RequestParameter... parameters)
   {
      List<UIParameter> list = new ArrayList<UIParameter>();
      if(parameters != null)
      {
         for (RequestParameter param : parameters)
         {
            UIParameter p = new UIParameter();
            if (param != null)
            {
               p.setValue(param.getName());
               p.setValue(param.getValue());
            }
            list.add(p);
         }
      }
      return build(mapping, false, list);
   }

   /**
    * Build a Pretty URL for the given Mapping ID and parameters.
    * 
    * @deprecated Use {@link #build(UrlMapping, boolean, List)} instead.
    */
   @Deprecated
   public String build(final UrlMapping urlMapping, final List<UIParameter> parameters)
   {
      return build(urlMapping, false, parameters);
   }
   
   /**
    * Build a Pretty URL for the given Mapping ID and parameters.
    * 
    * @since 3.2.0
    */
   public String build(final UrlMapping urlMapping, final boolean encodeUrl, final List<UIParameter> parameters)
   {
      String result = "";
      if (urlMapping != null)
      {
         URLPatternParser parser = urlMapping.getPatternParser();
         List<String> pathParams = new ArrayList<String>();
         List<QueryParameter> queryParams = new ArrayList<QueryParameter>();

         if(parameters != null)
         {
            // TODO this logic should be in the components, not in the builder
            if (parameters.size() == 1)
            {
               UIParameter firstParam = parameters.get(0);
               if (((firstParam.getValue() != null)) && (firstParam.getName() == null))
               {
                  if (firstParam.getValue() instanceof List<?>)
                  {
                     URL url = parser.getMappedURL(firstParam.getValue());
                     return url.toURL();
                  }
                  else if (firstParam.getValue().getClass().isArray())
                  {
                     // The Object[] cast here is required, otherwise Java treats
                     // getValue() as a single Object.
                     List<Object> list = Arrays.asList((Object[]) firstParam.getValue());
                     URL url = parser.getMappedURL(list);
                     return url.toURL();
                  }
               }
            }
   
            for (UIParameter parameter : parameters)
            {
               String name = parameter.getName();
               Object value = parameter.getValue();
   
               if ((name == null) && (value != null))
               {
                  pathParams.add(value.toString());
               }
               else
               {
                  List<?> values = null;
                  if ((value != null) && value.getClass().isArray())
                  {
                     values = Arrays.asList((Object[]) value);
                  }
                  else if (value instanceof List<?>)
                  {
                     values = (List<?>) value;
                  }
                  else if (value != null)
                  {
                     values = Arrays.asList(value);
                  }
   
                  if (values != null)
                  {
                     for (Object object : values)
                     {
                        String tempValue = null;
                        if (object != null)
                        {
                           tempValue = object.toString();
                        }
                        queryParams.add(new QueryParameter(name, tempValue));
                     }
                  }
                  else
                  {
                     queryParams.add(new QueryParameter(name, null));
                  }
               }
            }
         }

         // build URL object for given path parameter set
         URL mappedURL = parser.getMappedURL(pathParams.toArray());
         
         // create encoded/unicode URL 
         String url = encodeUrl ? mappedURL.encode().toURL() : mappedURL.toURL();
         
         // append query string
         result = url + QueryString.build(queryParams).toQueryString();
         
      }
      return result;
   }
}
