package org.ocpsoft.urlbuilder;

public class AddressBuilderBase
{
   private AddressBuilder parent;

   AddressBuilderBase(AddressBuilder parent)
   {
      this.parent = parent;
   }

   public Address build()
   {
      return parent.build();
   }

   public AddressBuilderProtocol protocol(CharSequence host)
   {
      return parent.protocol(host);
   }

   public AddressBuilderHost host(CharSequence host)
   {
      return parent.host(host);
   }

   public AddressBuilderPort port(int port)
   {
      return parent.port(port);
   }

   public AddressBuilderPath path(CharSequence path)
   {
      return parent.path(path);
   }

   public AddressBuilderPath pathEncoded(CharSequence path)
   {
      return parent.pathEncoded(path);
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

   @Override
   public String toString()
   {
      return parent.toString();
   }

}
