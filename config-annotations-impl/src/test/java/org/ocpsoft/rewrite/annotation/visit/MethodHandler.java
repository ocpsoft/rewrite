package org.ocpsoft.rewrite.annotation.visit;

import java.lang.reflect.Method;

import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.annotation.spi.MethodAnnotationHandler;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class MethodHandler extends MethodAnnotationHandler<MethodAnno>
{
   @Override
   public Class<MethodAnno> handles()
   {
      return MethodAnno.class;
   }

   @Override
   public void process(MethodContext context, Method element, MethodAnno annotation)
   {
      context.getConfigurationBuilder().addRule()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(202));
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
