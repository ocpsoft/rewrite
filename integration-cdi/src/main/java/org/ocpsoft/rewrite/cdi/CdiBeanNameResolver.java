package org.ocpsoft.rewrite.cdi;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * Implementation of {@link BeanNameResolver} for CDI.
 * 
 * @author Christian Kaltepoth
 */
public class CdiBeanNameResolver implements BeanNameResolver
{

   private final Logger log = Logger.getLogger(CdiBeanNameResolver.class);

   @Inject
   private BeanManager beanManager;

   @Override
   public String getBeanName(Class<?> clazz)
   {

      Set<Bean<?>> beans = beanManager.getBeans(clazz);

      // no matching beans, the BeanManager doesn't know something about this class
      if (beans == null || beans.size() == 0) {
         return null;
      }

      // more than one result -> warn the user
      else if (beans.size() > 1) {
         log.warn("The BeanManager returns more than one name for [{}]", clazz.getName());
         return null;
      }

      // exactly one result -> we got a name
      else {
         return beans.iterator().next().getName();
      }

   }

}
