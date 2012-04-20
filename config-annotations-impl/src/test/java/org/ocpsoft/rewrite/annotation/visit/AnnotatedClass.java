package org.ocpsoft.rewrite.annotation.visit;

@TypeAnno("type")
public class AnnotatedClass
{
   @FieldAnno("field")
   public boolean field;

   @MethodAnno("method")
   public void method(@ParamAnno("parameter") int param)
   {

   }
}
