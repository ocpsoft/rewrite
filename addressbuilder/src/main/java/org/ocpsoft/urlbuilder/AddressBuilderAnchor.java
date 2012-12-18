package org.ocpsoft.urlbuilder;

public class AddressBuilderAnchor
{
   private AddressBuilder parent;

   AddressBuilderAnchor(AddressBuilder parent)
   {
      this.parent = parent;
   }

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
