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
public class AddressBuilderSchemeSpecificPart
{
   private AddressBuilder parent;

   AddressBuilderSchemeSpecificPart(AddressBuilder parent)
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

   @Override
   public String toString()
   {
      return parent.toString();
   }

}
