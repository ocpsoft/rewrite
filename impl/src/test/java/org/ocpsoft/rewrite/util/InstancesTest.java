package org.ocpsoft.rewrite.util;

import org.junit.Test;
import org.ocpsoft.common.util.Assert;

public class InstancesTest
{
   @Test
   public void testDefaultInstanceProvider()
   {
      InstancesTest test = Instances.lookup(InstancesTest.class);
      Assert.notNull(test, "Instance was null.");
   }

}
