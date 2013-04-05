package org.ocpsoft.rewrite.faces.artifact;

import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.faces.util.NullComponent;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.spi.ConverterProvider;
import org.ocpsoft.rewrite.spi.ValidatorProvider;

/**
 * {@link ConverterProvider} that allows to use JSF converters with Rewrite.
 * 
 * @author Christian Kaltepoth
 */
public class FacesValidatorProvider implements ValidatorProvider
{

   @Override
   public Validator<?> getByTargetType(Class<?> targetType)
   {
      // unsupported
      return null;
   }

   @Override
   public Validator<?> getByValidatorId(String id)
   {

      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
               "You should use @Deferred so the binding gets executed within the JSF lifecycle.");

      javax.faces.validator.Validator validator = facesContext.getApplication().createValidator(id);

      if (validator!= null) {
         return FacesValidatorAdapter.from(validator);
      }

      return null;

   }

   @Override
   public Validator<?> getByValidatorType(Class<?> validatorType)
   {
      // unsupported
      return null;
   }

   private static class FacesValidatorAdapter<T> implements Validator<T>
   {

      private final javax.faces.validator.Validator validator;

      private FacesValidatorAdapter(javax.faces.validator.Validator validator)
      {
         this.validator = validator;
      }

      public static <T> FacesValidatorAdapter<T> from(javax.faces.validator.Validator validator)
      {
         return new FacesValidatorAdapter<T>(validator);
      }

      @Override
      public boolean isValid(Rewrite event, EvaluationContext context, T value)
      {
         FacesContext facesContext = FacesContext.getCurrentInstance();
         Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
                  "You should use @Deferred so the binding gets executed within the JSF lifecycle.");

         try {
            validator.validate(facesContext, NullComponent.getCurrentComponent(facesContext), value);
         }
         catch (ValidatorException e) {
            return false;
         }
         return true;
      }
      
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
