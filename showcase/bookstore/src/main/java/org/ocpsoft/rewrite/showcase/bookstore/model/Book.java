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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "book")
public class Book extends AbstractEntity
{

   private static final long serialVersionUID = 1L;

   @Basic
   private String title;

   @Basic
   private String author;

   @Basic
   private Long isbn;

   @Basic
   private Integer year;

   @Basic
   private Float price;

   @Basic
   @Column(name = "seo_key")
   private String seoKey;

   @ManyToOne
   @JoinColumn(name = "category_id")
   private Category category;

   @Basic
   private Integer stock;

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

}
