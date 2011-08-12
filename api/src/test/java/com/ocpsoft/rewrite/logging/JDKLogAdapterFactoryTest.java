package com.ocpsoft.rewrite.logging;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class JDKLogAdapterFactoryTest
{

   @Test
   public void testJDKCreateLogAdapter()
   {
      Logger log = new JDKLogAdapterFactory().createLogAdapter(JDKLogAdapterFactoryTest.class.getName());
      assertEquals(JDKLogAdapter.class, log.getClass());
   }

}
