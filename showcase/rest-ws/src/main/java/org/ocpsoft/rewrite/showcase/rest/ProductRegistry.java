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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@ApplicationScoped
@XmlRootElement(name = "products")
public class ProductRegistry
{
   private final List<Product> products = new ArrayList<Product>();

   public ProductRegistry()
   {
      add(new Product("Football Ticket", "Tickets to see your favorite football team. We know your favorite.",
               65.99));
      add(new Product("Baseball Cap", "Always stylish, sometimes practical; forward use only.", 15.99));
      add(new Product("Snug-fit Swim Trunks", "Somehow not as interesting as the female counterpart.", 20.99));
      add(new Product("Purple 2-Piece Bikini", "Who doesn't like purple?", 30.99));
   }

   public Product getById(final int id)
   {
      if ((id < 0) || (id > (products.size() - 1)))
      {
         throw new RuntimeException("No product with id [" + id + "]");
      }
      return products.get(id);
   }

   @XmlElement(name = "product", type = Product.class)
   public List<Product> getProducts()
   {
      return products;
   }

   public Product add(final Product product)
   {
      products.add(product);
      product.setId(products.indexOf(product));
      return product;
   }

}
