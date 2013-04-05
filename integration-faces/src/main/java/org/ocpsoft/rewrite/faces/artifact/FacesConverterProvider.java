package org.ocpsoft.rewrite.faces.artifact;

import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.faces.util.NullComponent;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.spi.ConverterProvider;

/**
 * {@link ConverterProvider} that allows to use JSF converters with Rewrite.
 * 
 * @author Christian Kaltepoth
 */
public class FacesConverterProvider implements ConverterProvider
{

   @Override
   public Converter<?> getByTargetType(Class<?> targetType)
   {

      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
               "You should use @Deferred so the binding gets executed within the JSF lifecycle.");

      javax.faces.convert.Converter converter = facesContext.getApplication().createConverter(targetType);

      if (converter != null) {
         return FacesConverterAdapter.from(converter);
      }

      return null;

   }

   @Override
   public Converter<?> getByConverterId(String id)
   {

      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
               "You should use @Deferred so the binding gets executed within the JSF lifecycle.");

      javax.faces.convert.Converter converter = facesContext.getApplication().createConverter(id);

      if (converter != null) {
         return FacesConverterAdapter.from(converter);
      }

      return null;

   }

   @Override
   public Converter<?> getByConverterType(Class<?> converterType)
   {
      // unsupported
      return null;
   }

   private static class FacesConverterAdapter<T> implements Converter<T>
   {

      private final javax.faces.convert.Converter converter;

      private FacesConverterAdapter(javax.faces.convert.Converter converter)
      {
         this.converter = converter;
      }

      public static <T> FacesConverterAdapter<T> from(javax.faces.convert.Converter converter)
      {
         return new FacesConverterAdapter<T>(converter);
      }

      @Override
      @SuppressWarnings("unchecked")
      public T convert(Rewrite event, EvaluationContext context, Object value)
      {

         FacesContext facesContext = FacesContext.getCurrentInstance();
         Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
                  "You should use @Deferred so the binding gets executed within the JSF lifecycle.");

         String valueAsString = value != null ? value.toString() : null;
         try {
            return (T) converter.getAsObject(facesContext, new NullComponent(), valueAsString);
         }
         catch (ConverterException e) {
            return null; // TODO: is this correct
         }
      }

   }

   @Override
   public int priority()
   {
      // TODO Auto-generated method stub
      return 0;
   }

}
