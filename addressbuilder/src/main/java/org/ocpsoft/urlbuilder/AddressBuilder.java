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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ocpsoft.urlbuilder.util.Decoder;
import org.ocpsoft.urlbuilder.util.Encoder;

/**
 * Representation of a uniform resource locator, or web address. Internal state is not encoded, plain UTF-8.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddressBuilder
{
   private volatile Address address;
   protected volatile CharSequence scheme;
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
         address = new AddressResult(this);
      }
      return address;
   }

   /**
    * Create a new {@link Address} from the given fully encoded URL. Improperly formatted or encoded URLs are not
    * parse-able and will result in an exception.
    * 
    * @throws IllegalArgumentException when the input URL or URL fragment is not valid.
    */
   public static Address create(String url) throws IllegalArgumentException
   {
      try {
         URI u = new URI(url);
         return AddressBuilder.begin().scheme(u.getScheme()).domain(u.getHost()).port(u.getPort())
                  .pathEncoded(u.getRawPath()).queryLiteral(u.getRawQuery()).anchor(u.getRawFragment()).build();
      }
      catch (URISyntaxException e) {
         throw new IllegalArgumentException(
                  "[" + url + "] is not a valid URL fragment. Consider encoding relevant portions of the URL with ["
                           + Encoder.class + "]", e);
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
    * Set the non-encoded path section of this {@link Address}. The given value will be stored without additional
    * encoding or decoding.
    */
   AddressBuilderPath path(CharSequence path)
   {
      this.path = path;
      return new AddressBuilderPath(this);
   }

   /**
    * Set the encoded path section of this {@link Address}. The given value will be decoded before it is stored.
    */
   AddressBuilderPath pathEncoded(CharSequence path)
   {
      this.path = Decoder.path(path);
      return new AddressBuilderPath(this);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values will be encoded before they are
    * stored.
    */
   AddressBuilderQuery query(CharSequence name, Object... values)
   {
      this.queries.put(Encoder.query(name.toString()), Parameter.create(name.toString(), true, values));
      return new AddressBuilderQuery(this);
   }

   /**
    * Set a pre-encoded query-parameter to a pre-encoded value or multiple values. The given name and values be stored
    * without additional encoding or decoding.
    */
   AddressBuilderQuery queryEncoded(CharSequence name, Object... values)
   {
      this.queries.put(name.toString(), Parameter.create(name.toString(), false, values));
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
            queryEncoded(entry.getKey(), entry.getValue().toArray());
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
    * Set a parameter name and value or values. Any supplied values will be encoded appropriately for their location in
    * the {@link Address}.
    */
   void set(CharSequence name, Object... values)
   {
      this.parameters.put(name.toString(), Parameter.create(name.toString(), true, values));
   }

   /**
    * Set a pre-encoded parameter name and value or values. The values will be stored with no additional encoding or
    * decoding.
    */
   void setEncoded(CharSequence name, Object... values)
   {
      this.parameters.put(name.toString(), Parameter.create(name.toString(), false, values));
   }

   @Override
   public String toString()
   {
      return build().toString();
   }
}
