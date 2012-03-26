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
package org.ocpsoft.rewrite.showcase.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@XmlRootElement
public class Product
{
   private int id;
   private String name;
   private String description;
   private double price;

   public Product(final String name, final String description, final double price)
   {
      super();
      this.name = name;
      this.description = description;
      this.price = price;
   }

   public Product()
   {}

   public int getId()
   {
      return id;
   }

   public void setId(final int id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(final String description)
   {
      this.description = description;
   }

   public double getPrice()
   {
      return price;
   }

   public void setPrice(final double price)
   {
      this.price = price;
   }
}
