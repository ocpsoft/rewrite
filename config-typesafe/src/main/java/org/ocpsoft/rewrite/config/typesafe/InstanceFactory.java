package org.ocpsoft.rewrite.config.typesafe;

import org.ocpsoft.common.pattern.Weighted;

/**
 * Responsible for performing object instance lookups.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public interface InstanceFactory extends Weighted
{
   /**
    * Get an instance of the given {@link Class} type. Return null if no instance could be retrieved.
    */
   Object getInstance(Class<?> type);
}
