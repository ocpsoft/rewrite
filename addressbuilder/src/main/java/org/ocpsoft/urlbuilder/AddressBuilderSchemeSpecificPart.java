/*
 * Copyright 2013 <a href="mailto:fabmars@gmail.com">Fabien Marsaudz</a>
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
 * An {@link Address} with a scheme specific part section.
 * 
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class AddressBuilderSchemeSpecificPart implements BuildableAddress
{
   private AddressBuilder parent;

   AddressBuilderSchemeSpecificPart(AddressBuilder parent)
   {
      this.parent = parent;
   }

   /**
    * Set a parameter name and value or values. The supplied values will be stored without additional encoding.
    */
   public AddressBuilderSchemeSpecificPart set(CharSequence name, Object... values)
   {
      parent.set(name, values);
      return this;
   }

   /**
    * Set a parameter name and value or values. The values will be decoded before they are stored.
    */
   public AddressBuilderSchemeSpecificPart setDecoded(CharSequence name, Object... values)
   {
      parent.setDecoded(name, values);
      return this;
   }

   /**
    * Set a parameter name and value or values. The values will be encoded before they are stored.
    */
   public AddressBuilderSchemeSpecificPart setEncoded(CharSequence name, Object... values)
   {
      parent.setEncoded(name, values);
      return this;
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

   @Override
   public String toString()
   {
      return parent.toString();
   }

}
