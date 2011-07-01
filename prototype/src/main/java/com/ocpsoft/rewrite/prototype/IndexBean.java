package com.ocpsoft.rewrite.prototype;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class IndexBean
{
   private String name;
   private String selected;

   public String handleSubmit(final Object i)
   {
      System.out.println("IndexBean.handleSubmit(" + i + ")");
      return "submitted";
   }

   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      System.out.println("Your name is " + name + "]");
      this.name = name;
   }

   public String getSelected()
   {
      return selected;
   }

   public void setSelected(final String selected)
   {
      System.out.println("You selected [" + selected + "]");
      this.selected = selected;
   }

}
