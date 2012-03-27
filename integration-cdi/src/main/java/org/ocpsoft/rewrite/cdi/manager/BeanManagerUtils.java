package org.ocpsoft.rewrite.cdi.manager;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class BeanManagerUtils
{
   @SuppressWarnings("unchecked")
   public static <T> T getContextualInstance(final BeanManager manager, final Class<T> type, Annotation... qualifiers)
   {
      T result = null;
      Bean<T> bean = (Bean<T>) manager.resolve(manager.getBeans(type, qualifiers));
      if (bean != null)
      {
         CreationalContext<T> context = manager.createCreationalContext(bean);
         if (context != null)
         {
            result = (T) manager.getReference(bean, type, context);
         }
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   public static CreationalContext<Object> injectNonContextualInstance(final BeanManager manager, final Object instance)
   {
      if (instance != null)
      {
         CreationalContext<Object> creationalContext = manager.createCreationalContext(null);
         InjectionTarget<Object> injectionTarget = (InjectionTarget<Object>) manager
                  .createInjectionTarget(manager.createAnnotatedType(instance.getClass()));
         injectionTarget.inject(instance, creationalContext);
         return creationalContext;
      }
      return null;
   }
}