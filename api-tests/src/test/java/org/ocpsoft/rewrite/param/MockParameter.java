package org.ocpsoft.rewrite.param;


public class MockParameter extends ParameterBuilder<MockParameter, String>
{
   private final String name;

   public MockParameter(String name)
   {
      this.name = name;
   }

   @Override
   public String getName()
   {
      return name;
   }
}