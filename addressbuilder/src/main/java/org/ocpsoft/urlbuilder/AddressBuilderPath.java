package org.ocpsoft.urlbuilder;

public class AddressBuilderPath
{
   private AddressBuilder parent;

   AddressBuilderPath(AddressBuilder parent)
   {
      this.parent = parent;
   }

   public Address build()
   {
      return parent.build();
   }

   public AddressBuilderQuery query(CharSequence name, Object... values)
   {
      return parent.query(name, values);
   }

   public AddressBuilderQuery queryEncoded(CharSequence name, Object... values)
   {
      return parent.queryEncoded(name, values);
   }

   public AddressBuilderQuery queryLiteral(String query)
   {
      return parent.queryLiteral(query);
   }

   public AddressBuilderAnchor anchor(String anchor)
   {
      return parent.anchor(anchor);
   }

   public AddressBuilderPath set(CharSequence name, Object... values)
   {
      parent.set(name, values);
      return this;
   }

   public AddressBuilderPath setEncoded(CharSequence name, Object... values)
   {
      parent.setEncoded(name, values);
      return this;
   }

   @Override
   public String toString()
   {
      return parent.toString();
   }
}
