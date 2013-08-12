package org.ocpsoft.rewrite.showcase.bookstore.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.ocpsoft.rewrite.showcase.bookstore.model.Book;
import org.ocpsoft.rewrite.showcase.bookstore.model.Category;

@ApplicationScoped
public class TestDataRepository
{

   private final List<Category> categories = new ArrayList<Category>();

   private final List<Book> books = new ArrayList<Book>();

   @PostConstruct
   public void init()
   {

      Category thriller = new Category();
      thriller.setId(1L);
      thriller.setName("Thriller");
      thriller.setSeoKey("thriller");
      thriller.setDescription("Thriller is a broad genre of literature, film, " +
               "and television programming that uses suspense, tension and excitement " +
               "as the main elements. Thrillers heavily stimulate the viewer's moods " +
               "giving them a high level of anticipation, ultra-heightened expectation, " +
               "uncertainty, surprise, anxiety and/or terror.");

      Category fantasy = new Category();
      fantasy.setId(2L);
      fantasy.setName("Fantasy");
      fantasy.setSeoKey("fantasy");
      fantasy.setDescription("Fantasy is a genre of fiction that commonly uses " +
               "magic and other supernatural phenomena as a primary element of plot, " +
               "theme, or setting. Many works within the genre take place in imaginary " +
               "worlds where magic is common.");

      categories.add(thriller);
      categories.add(fantasy);

      Book book1 = new Book();
      book1.setId(101L);
      book1.setCategory(thriller);
      book1.setPrice(8.99f);
      book1.setStock(3);
      book1.setYear(2012);
      book1.setTitle("The Bubble Gum Thief");
      book1.setAuthor("Jeff Miller");
      book1.setIsbn(9781612184838L);

      Book book2 = new Book();
      book2.setId(102L);
      book2.setCategory(thriller);
      book2.setPrice(15.99f);
      book2.setStock(1);
      book2.setYear(2012);
      book2.setTitle("May We Be Forgiven: A Novel");
      book2.setAuthor("A. M. Homes");
      book2.setIsbn(9780670025480L);

      books.add(book1);
      books.add(book2);

   }

   public List<Category> getCategories()
   {
      return categories;
   }

   public List<Book> getBooks()
   {
      return books;
   }

}
