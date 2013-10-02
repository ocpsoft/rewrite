package org.ocpsoft.rewrite.annotation.visit;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class TypeHandler implements AnnotationHandler<TypeAnno>
{
   @Override
   public Class<TypeAnno> handles()
   {
      return TypeAnno.class;
   }

   @Override
   public void process(ClassContext context, TypeAnno annotation, HandlerChain chain)
   {

      // TODO: quickfix to tell context that we created a rule
      context.getRuleBuilder();

      context.getConfigurationBuilder().addRule()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(204));
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
