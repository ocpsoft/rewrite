package com.ocpsoft.rewrite.logging;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import com.ocpsoft.logging.JDKLogAdapter;
import com.ocpsoft.logging.JDKLogAdapterFactory;
import com.ocpsoft.logging.Logger;

public class JDKLogAdapterFactoryTest
{

   @Test
   public void testJDKCreateLogAdapter()
   {
      Logger log = new JDKLogAdapterFactory().createLogAdapter(JDKLogAdapterFactoryTest.class.getName());
      assertEquals(JDKLogAdapter.class, log.getClass());
   }

}
