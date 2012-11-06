package org.ocpsoft.rewrite.instance;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.spi.InstanceProvider;

/**
 * Default implementation of {@link InstanceProvider} using Java reflection.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class DefaultInstanceProvider implements InstanceProvider
{
   private static Logger log = Logger.getLogger(DefaultInstanceProvider.class);

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Object getInstance(Class<?> type)
   {
      Object result = null;
      try {
         result = type.newInstance();
      }
      catch (Exception e) {
         log.debug("Could not create instance of type [" + type.getName() + "] through reflection.", e);
      }
      return result;
   }

}
