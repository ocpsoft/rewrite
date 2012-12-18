package org.ocpsoft.urlbuilder;

public class AddressBuilderQuery
{
   private AddressBuilder parent;

   AddressBuilderQuery(AddressBuilder parent)
   {
      this.parent = parent;
   }

   public Address build()
   {
      return parent.build();
   }

   public AddressBuilderAnchor anchor(CharSequence anchor)
   {
      return parent.anchor(anchor);
   }

   public AddressBuilderQuery query(CharSequence name, Object value)
   {
      return parent.query(name, value);
   }

   public AddressBuilderQuery queryEncoded(CharSequence name, Object value)
   {
      return parent.queryEncoded(name, value);
   }

   public AddressBuilderQuery queryLiteral(String query)
   {
      return parent.queryLiteral(query);
   }

   @Override
   public String toString()
   {
      return parent.toString();
   }
}
