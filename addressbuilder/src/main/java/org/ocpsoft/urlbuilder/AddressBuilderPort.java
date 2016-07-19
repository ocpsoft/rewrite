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
 * An {@link Address} with a port section.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddressBuilderPort implements BuildableAddress
{
   private AddressBuilder parent;

   AddressBuilderPort(AddressBuilder parent)
   {
      this.parent = parent;
   }

   @Override
   public Address build()
   {
      return parent.build();
   }

   @Override
   public Address buildLiteral()
   {
      return parent.buildLiteral();
   }

   /**
    * Set the path section of this {@link Address}. The given value will be stored without additional
    * encoding or decoding.
    */
   public AddressBuilderPath path(CharSequence path)
   {
      return parent.path(path);
   }

   /**
    * Set the path section of this {@link Address}. The given value will be decoded before it is stored.
    */
   public AddressBuilderPath pathDecoded(CharSequence path)
   {
      return parent.pathDecoded(path);
   }

   /**
    * Set the path section of this {@link Address}. The given value will be encoded before it is stored.
    */
   public AddressBuilderPath pathEncoded(CharSequence path)
   {
      return parent.pathDecoded(path);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values will be stored without additional
    * encoding or decoding.
    */
   public AddressBuilderQuery query(CharSequence name, Object... values)
   {
      return parent.query(name, values);
   }

   /**
    * Set a query-parameter value or multiple values. The given name and values be decoded before they are stored.
    */
   public AddressBuilderQuery queryDecoded(CharSequence name, Object... values)
   {
      return parent.queryDecoded(name, values);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values be encoded before they are stored.
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
