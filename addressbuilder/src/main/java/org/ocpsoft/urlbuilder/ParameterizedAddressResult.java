/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.urlbuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ocpsoft.urlbuilder.util.CaptureType;
import org.ocpsoft.urlbuilder.util.CapturingGroup;
import org.ocpsoft.urlbuilder.util.ParseTools;

/**
 * Parameterized implementation of {@link Address} created by {@link AddressBuilder}. (Applies parameterization to all
 * parts of the URL.)
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class ParameterizedAddressResult implements Address
{
   private final String protocol;
   private final String schemeSpecificPart;
   private final String host;
   private final Integer port;
   private final String path;
   private final String query;
   private final String anchor;
   private CharSequence result;
   private Map<String, List<Object>> queries = Collections.emptyMap();

   public ParameterizedAddressResult(AddressBuilder parent)
   {
      if (isSet(parent.scheme))
         protocol = parameterize(parent.parameters, parent.scheme).toString();
      else
         protocol = null;

      if (isSet(parent.schemeSpecificPart))
         schemeSpecificPart = parameterize(parent.parameters, parent.schemeSpecificPart).toString();
      else
         schemeSpecificPart = null;

      if (isSet(parent.domain))
         host = parameterize(parent.parameters, parent.domain).toString();
      else
         host = null;

      if (isSet(parent.port))
         port = parent.port;
      else
         port = null;

      if (isSet(parent.path))
      {
         CharSequence path = parameterize(parent.parameters, parent.path);
         if (path.charAt(0) != '/')
            path = new StringBuilder('/').append(path);
         this.path = path.toString();
      }
      else
         path = null;

      if (isSet(parent.queries))
      {
         this.queries = Collections.unmodifiableMap(parent.getQueries());
         query = toQuery(parent.queries).toString();
      }
      else
         query = null;

      if (isSetOrEmpty(parent.anchor))
         anchor = parameterize(parent.parameters, parent.anchor).toString();
      else
         anchor = null;
   }

   private CharSequence toQuery(Map<CharSequence, Parameter> queries)
   {
      StringBuilder result = new StringBuilder();
      boolean first = true;
      for (CharSequence name : queries.keySet()) {
         Parameter parameter = queries.get(name);

         if (!first)
            result.append('&');
         else
            first = false;

         result.append(name);

         if (parameter.getValueCount() > 0)
         {
            for (int i = 0; i < parameter.getValueCount(); i++) {
               String value = parameter.getValue(i);

               if (value != null)
                  result.append('=').append(value);

               if (i < parameter.getValueCount() - 1)
               {
                  result.append('&').append(name);
               }
            }
         }
      }
      return result;
   }

   @Override
   public String toString()
   {
      if (this.result == null)
      {
         StringBuilder result = AddressBuilder.toString(this);
         this.result = result;
      }

      return this.result.toString();
   }

   private CharSequence parameterize(Map<CharSequence, Parameter> parameters, CharSequence sequence)
   {
      StringBuilder result = new StringBuilder();
      int cursor = 0;
      int lastEnd = 0;
      while (cursor < sequence.length())
      {
         switch (sequence.charAt(cursor))
         {
         case '{':
            result.append(sequence.subSequence(lastEnd, cursor));

            int startPos = cursor;
            CapturingGroup group = ParseTools.balancedCapture(sequence, startPos, sequence.length() - 1,
                     CaptureType.BRACE);
            cursor = group.getEnd();
            lastEnd = group.getEnd() + 1;

            String name = group.getCaptured().toString();

            Parameter parameter = parameters.get(name);
            if (parameter == null || !parameter.hasValues())
               throw new IllegalStateException("No parameter [" + name + "] was set in the pattern [" + sequence
                        + "]. Call address.set(\"" + name + "\", value); or remove the parameter from the pattern.");

            result.append(parameter.getValue(0));

            break;

         default:
            break;
         }

         cursor++;
      }

      if (cursor >= lastEnd)
      {
         result.append(sequence.subSequence(lastEnd, cursor));
      }
      return result;
   }

   private boolean isSet(Integer port)
   {
      return port != null;
   }

   private boolean isSet(Map<?, ?> map)
   {
      return map != null && !map.isEmpty();
   }

   private boolean isSet(CharSequence value)
   {
      return value != null && value.length() > 0;
   }

   private boolean isSetOrEmpty(CharSequence value)
   {
      return value != null;
   }

   /*
    * Inspectors
    */

   @Override
   public String getAnchor()
   {
      return anchor;
   }

   @Override
   public boolean isAnchorSet()
   {
      return isSetOrEmpty(anchor);
   }

   @Override
   public String getPath()
   {
      return path;
   }

   @Override
   public String getPathAndQuery()
   {
      StringBuilder result = new StringBuilder();
      if (isPathSet())
         result.append(getPath());
      if (isQuerySet())
         result.append('?').append(getQuery());
      return result.toString();
   }

   @Override
   public boolean isPathSet()
   {
      return isSet(path);
   }

   @Override
   public Integer getPort()
   {
      return port;
   }

   @Override
   public boolean isPortSet()
   {
      return isSet(port);
   }

   @Override
   public String getDomain()
   {
      return host;
   }

   @Override
   public boolean isDomainSet()
   {
      return isSet(host);
   }

   @Override
   public String getScheme()
   {
      return protocol;
   }

   @Override
   public boolean isSchemeSet()
   {
      return isSet(protocol);
   }

   @Override
   public String getSchemeSpecificPart()
   {
      return schemeSpecificPart;
   }

   @Override
   public boolean isSchemeSpecificPartSet()
   {
      return isSet(schemeSpecificPart);
   }

   @Override
   public String getQuery()
   {
      return query;
   }

   @Override
   public Map<String, List<Object>> getQueryParameters()
   {
      return queries;
   }

   @Override
   public boolean isQuerySet()
   {
      return isSet(query);
   }
}
