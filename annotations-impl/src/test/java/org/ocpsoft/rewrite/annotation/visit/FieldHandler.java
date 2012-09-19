package org.ocpsoft.rewrite.annotation.visit;

import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.FieldAnnotationHandler;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class FieldHandler extends FieldAnnotationHandler<FieldAnno>
{
   @Override
   public Class<FieldAnno> handles()
   {
      return FieldAnno.class;
   }

   @Override
   public void process(FieldContext context, FieldAnno annotation, HandlerChain chain)
   {
      context.getConfigurationBuilder().addRule()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(201));
      chain.proceed();
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
