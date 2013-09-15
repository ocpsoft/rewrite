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

/**
 * An {@link Address} with a scheme section.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddressBuilderScheme
{
   private AddressBuilder parent;

   AddressBuilderScheme(AddressBuilder parent)
   {
      this.parent = parent;
   }

   /**
    * Generate an {@link Address} representing the current state of this {@link AddressBuilder}.
    */
   public Address build()
   {
      return parent.build();
   }

   /**
    * Set the domain section of this {@link Address}.
    */
   public AddressBuilderDomain domain(CharSequence host)
   {
      return parent.domain(host);
   }

   /**
    * Set the scheme specific part section of this {@link Address}.
    */
   public AddressBuilderSchemeSpecificPart schemeSpecificPart(CharSequence schemeSpecificPart)
   {
      return parent.schemeSpecificPart(schemeSpecificPart);
   }
   
   /**
    * Set the port section of this {@link Address}.
    */
   public AddressBuilderPort port(int port)
   {
      return parent.port(port);
   }

   /**
    * Set the non-encoded path section of this {@link Address}. The given value will be stored without additional
    * encoding or decoding.
    */
   public AddressBuilderPath path(CharSequence path)
   {
      return parent.path(path);
   }

   /**
    * Set the encoded path section of this {@link Address}. The given value will be decoded before it is stored.
    */
   public AddressBuilderPath pathEncoded(CharSequence path)
   {
      return parent.pathEncoded(path);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values will be encoded before they are
    * stored.
    */
   public AddressBuilderQuery query(CharSequence name, Object... values)
   {
      return parent.query(name, values);
   }

   /**
    * Set a pre-encoded query-parameter to a pre-encoded value or multiple values. The given name and values be stored
    * without additional encoding or decoding.
    */
   public AddressBuilderQuery queryEncoded(CharSequence name, Object... values)
   {
      return parent.queryEncoded(name, values);
   }

   /**
    * Set a literal query string without additional encoding or decoding. A leading '?' character is optional; the
    * builder will add one if necessary.
    */
   public AddressBuilderQuery queryLiteral(String query)
   {
      return parent.queryLiteral(query);
   }

   /**
    * Set the anchor section of this {@link Address}.
    */
   public AddressBuilderAnchor anchor(String anchor)
   {
      return parent.anchor(anchor);
   }

   @Override
   public String toString()
   {
      return parent.toString();
   }

}
