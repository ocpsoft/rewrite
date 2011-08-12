package com.ocpsoft.rewrite.logging.slf4j;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.ocpsoft.rewrite.logging.Log;
import com.ocpsoft.rewrite.logging.LoggerFactory;

public class SLF4JLogAdapterFactoryTest
{

   @Test
   public void testSLF4JAdapterPreferedOverJDKLogger()
   {
      Log log = LoggerFactory.getLog(this.getClass());
      assertTrue(log instanceof SLF4JLogAdapter);
   }

}
