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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ocpsoft.urlbuilder.util.Decoder;
import org.ocpsoft.urlbuilder.util.Encoder;

/**
 * Representation of a uniform resource locator, or web address. Internal state is stored as it is originally provided,
 * and must be encoded or decoded as necessary.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddressBuilder
{
   private volatile Address address;
   protected volatile CharSequence scheme;
   protected volatile CharSequence schemeSpecificPart;
   protected volatile CharSequence domain;
   protected volatile Integer port;
   protected volatile CharSequence path;
   protected Map<CharSequence, Parameter> parameters = new LinkedHashMap<CharSequence, Parameter>();
   protected Map<CharSequence, Parameter> queries = new LinkedHashMap<CharSequence, Parameter>();
   protected CharSequence anchor;

   protected AddressBuilder()
   {}

   /**
    * Create a new {@link AddressBuilder} instance.
    */
   public static AddressBuilderBase begin()
   {
      return new AddressBuilderBase(new AddressBuilder());
   }

   /**
    * Generate an {@link Address} representing the current state of this {@link AddressBuilder}.
    */
   protected Address build()
   {
      if (address == null)
      {
         address = new ParameterizedAddressResult(this);
      }
      return address;
   }

   /**
    * Generate an {@link Address} representing the current literal state of this {@link AddressBuilder}.
    * <p>
    * (Does not apply parameterization. E.g. The URL `/{foo}` will be treated as literal text, as opposed to calling
    * {@link #build()}, which would result in `foo` being treated as a parameterized expression)
    */
   protected Address buildLiteral()
   {
      if (address == null)
      {
         address = new AddressResult(this);
      }
      return address;
   }

   /**
    * Create a new {@link Address} from the given URL. Improperly formatted or encoded URLs are not parse-able and will
    * result in an exception. No builder parameterization is possible using this method.
    * 
    * @see http://en.wikipedia.org/wiki/URI_scheme
    * @throws IllegalArgumentException when the input URL or URL fragment is not valid.
    */
   public static Address create(String url) throws IllegalArgumentException
   {
      try {
         URI u = new URI(url);
         String scheme = u.getScheme();
         String host = u.getHost();
         if (scheme != null && host == null)
            return AddressBuilder.begin().scheme(u.getScheme()).schemeSpecificPart(u.getRawSchemeSpecificPart())
                     .buildLiteral();
         else
            return AddressBuilder.begin().scheme(scheme).domain(host).port(u.getPort())
                     .path(u.getRawPath()).queryLiteral(u.getRawQuery()).anchor(u.getRawFragment()).buildLiteral();
      }
      catch (URISyntaxException e) {
         throw new IllegalArgumentException(
                  "[" + url + "] is not a valid URL fragment. Consider encoding relevant portions of the URL with ["
                           + Encoder.class
                           + "], or use the provided builder pattern via this class to specify part encoding.", e);
      }
   }

   /**
    * Set the scheme section of this {@link Address}.
    */
   AddressBuilderScheme scheme(CharSequence scheme)
   {
      this.scheme = scheme;
      return new AddressBuilderScheme(this);
   }

   /**
    * Set the scheme section of this {@link Address}.
    */
   AddressBuilderSchemeSpecificPart schemeSpecificPart(CharSequence schemeSpecificPart)
   {
      this.schemeSpecificPart = schemeSpecificPart;
      return new AddressBuilderSchemeSpecificPart(this);
   }

   /**
    * Set the domain section of this {@link Address}.
    */
   AddressBuilderDomain domain(CharSequence domain)
   {
      this.domain = domain;
      return new AddressBuilderDomain(this);
   }

   /**
    * Set the port section of this {@link Address}.
    */
   AddressBuilderPort port(int port)
   {
      if (port >= 0)
         this.port = port;
      return new AddressBuilderPort(this);
   }

   /**
    * Set the path section of this {@link Address}. The given value will be stored without additional encoding or
    * decoding.
    */
   AddressBuilderPath path(CharSequence path)
   {
      this.path = path;
      return new AddressBuilderPath(this);
   }

   /**
    * Set the path section of this {@link Address}. The given value will be decoded before it is stored.
    */
   AddressBuilderPath pathDecoded(CharSequence path)
   {
      this.path = Decoder.path(path);
      return new AddressBuilderPath(this);
   }

   /**
    * Set the path section of this {@link Address}. The given value will be encoded before it is stored.
    */
   AddressBuilderPath pathEncoded(CharSequence path)
   {
      this.path = Encoder.path(path);
      return new AddressBuilderPath(this);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values will be stored without additional
    * encoding or decoding.
    */
   AddressBuilderQuery query(CharSequence name, Object... values)
   {
      if (name != null && values != null)
      {
         this.queries.put(name.toString(), Parameter.create(name.toString(), values));
      }
      return new AddressBuilderQuery(this);
   }

   /**
    * Set a query-parameter value or multiple values. The given name and values be decoded before they are stored.
    */
   AddressBuilderQuery queryDecoded(CharSequence name, Object... values)
   {
      if (name != null && values != null)
      {
         List<Object> encodedValues = new ArrayList<Object>(values.length);
         for (Object value : values)
         {
            if (value == null)
               encodedValues.add(value);
            else
               encodedValues.add(Decoder.query(value.toString()));
         }
         this.queries.put(Decoder.query(name.toString()), Parameter.create(name.toString(), encodedValues));
      }
      return new AddressBuilderQuery(this);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values be encoded before they are stored.
    */
   AddressBuilderQuery queryEncoded(CharSequence name, Object... values)
   {
      if (name != null && values != null)
      {
         List<Object> encodedValues = new ArrayList<Object>(values.length);
         for (Object value : values)
         {
            if (value == null)
               encodedValues.add(value);
            else
               encodedValues.add(Encoder.query(value.toString()));
         }
         this.queries.put(Encoder.query(name.toString()), Parameter.create(name.toString(), encodedValues));
      }
      return new AddressBuilderQuery(this);
   }

   /**
    * Set a literal query string without additional encoding or decoding. A leading '?' character is optional; the
    * builder will add one if necessary.
    */
   AddressBuilderQuery queryLiteral(String query)
   {
      if (query != null)
      {
         if (query.startsWith("?"))
            query = query.substring(1);

         /*
          * Linked hash map is important here in order to retain the order of query parameters.
          */
         Map<CharSequence, List<CharSequence>> params = new LinkedHashMap<CharSequence, List<CharSequence>>();
         query = decodeHTMLAmpersands(query);
         int index = 0;

         while ((index = query.indexOf('&')) >= 0 || !query.isEmpty())
         {
            String pair = query;
            if (index >= 0)
            {
               pair = query.substring(0, index);
               query = query.substring(index);
               if (!query.isEmpty())
                  query = query.substring(1);
            }
            else
               query = "";

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

            List<CharSequence> list = params.get(name);
            if (list == null)
            {
               list = new ArrayList<CharSequence>();
               params.put(name, list);
            }
            list.add(value);
         }

         for (Entry<CharSequence, List<CharSequence>> entry : params.entrySet()) {
            query(entry.getKey(), entry.getValue().toArray());
         }
      }
      return new AddressBuilderQuery(this);
   }

   private String decodeHTMLAmpersands(String url)
   {
      if (url != null)
      {
         int index = 0;
         while ((index = url.indexOf("&amp;")) >= 0)
         {
            url = url.substring(0, index + 1) + url.substring(index + 5);
         }
      }
      return url;
   }

   /**
    * Set the anchor section of this {@link Address}.
    */
   AddressBuilderAnchor anchor(CharSequence anchor)
   {
      this.anchor = anchor;
      return new AddressBuilderAnchor(this);
   }

   /**
    * Set a parameter name and value or values. The supplied values will be stored without additional encoding.
    */
   void set(CharSequence name, Object... values)
   {
      this.parameters.put(name.toString(), Parameter.create(name.toString(), values));
   }

   /**
    * Set a parameter name and value or values. The values will be decoded before they are stored.
    */
   void setDecoded(CharSequence name, Object... values)
   {
      if (name != null && values != null)
      {
         List<Object> encodedValues = new ArrayList<Object>(values.length);
         for (Object value : values)
         {
            if (value == null)
               encodedValues.add(value);
            else
               encodedValues.add(Decoder.path(value.toString()));
         }
         this.parameters.put(name.toString(), Parameter.create(name.toString(), encodedValues));
      }
   }

   /**
    * Set a parameter name and value or values. The values will be encoded before they are stored.
    */
   void setEncoded(CharSequence name, Object... values)
   {
      if (name != null && values != null)
      {
         List<Object> encodedValues = new ArrayList<Object>(values.length);
         for (Object value : values)
         {
            if (value == null)
               encodedValues.add(value);
            else
               encodedValues.add(Encoder.path(value.toString()));
         }
         this.parameters.put(name.toString(), Parameter.create(name.toString(), encodedValues));
      }
   }

   @Override
   public String toString()
   {
      return buildLiteral().toString();
   }

   /**
    * Package private method for {@link Address} implementations to use for rendering.
    */
   static StringBuilder toString(Address address)
   {
      StringBuilder result = new StringBuilder();

      if (address.isSchemeSet())
         result.append(address.getScheme()).append(":");

      if (address.isSchemeSpecificPartSet())
      {
         result.append(address.getSchemeSpecificPart());
      }
      else
      {
         if (address.isDomainSet())
            result.append("//").append(address.getDomain());

         if (address.isPortSet())
            result.append(":").append(address.getPort());

         if (address.isPathSet())
            result.append(address.getPath());

         if (address.isQuerySet())
         {
            if (address.isDomainSet() && !address.isPathSet())
               result.append("/");
            result.append('?').append(address.getQuery());
         }

         if (address.isAnchorSet())
            result.append('#').append(address.getAnchor());
      }
      return result;
   }

   Map<String, List<Object>> getQueries()
   {
      Map<String, List<Object>> result = new LinkedHashMap<String, List<Object>>();
      for (Entry<CharSequence, Parameter> entry : this.queries.entrySet()) {
         CharSequence key = entry.getKey();
         result.put(key == null ? null : key.toString(), entry.getValue().getValues());
      }
      return Collections.unmodifiableMap(result);
   }
}
