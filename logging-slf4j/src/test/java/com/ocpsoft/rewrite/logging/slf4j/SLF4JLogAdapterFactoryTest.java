package com.ocpsoft.rewrite.logging.slf4j;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.ocpsoft.rewrite.logging.Logger;

public class SLF4JLogAdapterFactoryTest
{

   @Test
   public void testSLF4JAdapterPreferedOverJDKLogger()
   {
      Logger log = Logger.getLogger(this.getClass());
      assertTrue(log instanceof SLF4JLogAdapter);
   }

}
