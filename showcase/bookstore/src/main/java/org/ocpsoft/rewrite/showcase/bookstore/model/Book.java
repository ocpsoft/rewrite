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
package org.ocpsoft.rewrite.showcase.bookstore.model;

import java.io.Serializable;

public class Book implements Serializable
{

   private static final long serialVersionUID = 1L;

   private long id;

   private String title;

   private String author;

   private Long isbn;

   private Integer year;

   private Float price;

   private String seoKey;

   private Category category;

   private Integer stock;

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public String getSeoKey()
   {
      return seoKey;
   }

   public void setSeoKey(String seoName)
   {
      this.seoKey = seoName;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public Category getCategory()
   {
      return category;
   }

   public void setCategory(Category category)
   {
      this.category = category;
   }

   public Long getIsbn()
   {
      return isbn;
   }

   public void setIsbn(Long isbn)
   {
      this.isbn = isbn;
   }

   public String getAuthor()
   {
      return author;
   }

   public void setAuthor(String author)
   {
      this.author = author;
   }

   public Float getPrice()
   {
      return price;
   }

   public void setPrice(Float price)
   {
      this.price = price;
   }

   public Integer getStock()
   {
      return stock;
   }

   public void setStock(Integer stock)
   {
      this.stock = stock;
   }

   public Integer getYear()
   {
      return year;
   }

   public void setYear(Integer year)
   {
      this.year = year;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Book other = (Book) obj;
      if (id != other.id)
         return false;
      return true;
   }

}
