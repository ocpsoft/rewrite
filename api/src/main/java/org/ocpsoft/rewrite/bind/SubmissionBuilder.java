package org.ocpsoft.rewrite.bind;

/**
 * Defines methods used when building {@link Submission} objects.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface SubmissionBuilder extends Submission
{
   /**
    * Set the {@link Converter} type with which this {@link Binding} value will be converted.
    */
   public SubmissionBuilder convertedBy(final Class<? extends Converter<?>> type);

   /**
    * Set the {@link Converter} with which this {@link Binding} value will be converter.
    */
   public SubmissionBuilder convertedBy(final Converter<?> converter);

   /**
    * Set the {@link Validator} type with which this {@link Binding} value will be validated.
    */
   public SubmissionBuilder validatedBy(final Class<? extends Validator<?>> type);

   /**
    * Set the {@link Validator} with which this {@link Binding} value will be validated.
    */
   public SubmissionBuilder validatedBy(final Validator<?> validator);

}