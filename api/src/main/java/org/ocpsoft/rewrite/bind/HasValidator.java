package org.ocpsoft.rewrite.bind;

/**
 * An object that can hold a {@link Validator}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface HasValidator<C>
{
   /**
    * Set the {@link Validator} type with which this {@link Binding} value will be validated.
    */
   public <X extends Validator<?>> C validatedBy(final Class<X> type);

   /**
    * Set the {@link Validator} with which this {@link Binding} value will be validated.
    */
   public C validatedBy(final Validator<?> validator);

   /**
    * Get the {@link Validator} with which this {@link Binding} value will be validated.
    */
   public Validator<?> getValidator();
}
