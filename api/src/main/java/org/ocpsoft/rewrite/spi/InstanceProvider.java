package org.ocpsoft.rewrite.spi;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.util.Instances;

/**
 * Responsible for performing object instance lookups. See {@link Instances#lookup(Class)} for convenient access to this
 * API.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface InstanceProvider extends Weighted
{
   /**
    * Get an instance of the given {@link Class} type. Return null if no instance could be retrieved.
    */
   Object getInstance(Class<?> type);
}
