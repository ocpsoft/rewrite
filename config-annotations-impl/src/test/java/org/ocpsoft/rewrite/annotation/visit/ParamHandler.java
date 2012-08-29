package org.ocpsoft.rewrite.annotation.visit;

import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.api.ParameterContext;
import org.ocpsoft.rewrite.annotation.spi.ParameterAnnotationHandler;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class ParamHandler extends ParameterAnnotationHandler<ParamAnno>
{
   @Override
   public Class<ParamAnno> handles()
   {
      return ParamAnno.class;
   }

   @Override
   public void process(ParameterContext context, ParamAnno annotation, HandlerChain chain)
   {
      context.getConfigurationBuilder().addRule()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(203));
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
