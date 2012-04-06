package org.ocpsoft.rewrite.bind;

/**
 * An object that can hold a {@link Converter}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface HasConverter<C>
{
   /**
    * Set the {@link Converter} type with which this {@link Binding} value will be converted.
    */
   public <X extends Converter<?>> C convertedBy(final Class<X> type);

   /**
    * Set the {@link Converter} with which this {@link Binding} value will be converted.
    */
   public C convertedBy(final Converter<?> converter);

   /**
    * Get the {@link Converter} with which this {@link Binding} value will be converted.
    */
   public Converter<?> getConverter();
}
