package org.ocpsoft.rewrite.util;

import org.junit.Test;
import org.ocpsoft.common.util.Assert;

public class DefaultInstanceProviderTest
{
   @Test
   public void testDefaultInstanceProvider()
   {
      DefaultInstanceProviderTest test = Instances.lookup(DefaultInstanceProviderTest.class);
      Assert.notNull(test, "Instance was null.");
   }

}
