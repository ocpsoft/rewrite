package org.ocpsoft.urlbuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
class AddressBuilderBase
{
   private AddressBuilder parent;

   AddressBuilderBase(AddressBuilder parent)
   {
      this.parent = parent;
   }

   /**
    * Generate an {@link Address} representing the current state of this {@link AddressBuilder}.
    */
   public Address build()
   {
      return parent.build();
   }

   /**
    * Set the scheme section of this {@link Address}.
    */
   public AddressBuilderScheme scheme(CharSequence protocol)
   {
      return parent.scheme(protocol);
   }

   /**
    * Set the domain section of this {@link Address}.
    */
   public AddressBuilderDomain domain(CharSequence domain)
   {
      return parent.domain(domain);
   }

   /**
    * Set the port section of this {@link Address}.
    */
   public AddressBuilderPort port(int port)
   {
      return parent.port(port);
   }

   /**
    * Set the non-encoded path section of this {@link Address}. The given value will be stored without additional
    * encoding or decoding.
    */
   public AddressBuilderPath path(CharSequence path)
   {
      return parent.path(path);
   }

   /**
    * Set the encoded path section of this {@link Address}. The given value will be decoded before it is stored.
    */
   public AddressBuilderPath pathEncoded(CharSequence path)
   {
      return parent.pathEncoded(path);
   }

   /**
    * Set a query-parameter to a value or multiple values. The given name and values will be encoded before they are
    * stored.
    */
   public AddressBuilderQuery query(CharSequence name, Object... values)
   {
      return parent.query(name, values);
   }

   /**
    * Set a pre-encoded query-parameter to a pre-encoded value or multiple values. The given name and values be stored
    * without additional encoding or decoding.
    */
   public AddressBuilderQuery queryEncoded(CharSequence name, Object... values)
   {
      return parent.queryEncoded(name, values);
   }

   /**
    * Set a literal query string without additional encoding or decoding. A leading '?' character is optional; the
    * builder will add one if necessary.
    */
   public AddressBuilderQuery queryLiteral(String query)
   {
      return parent.queryLiteral(query);
   }

   /**
    * Set the anchor section of this {@link Address}.
    */
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
