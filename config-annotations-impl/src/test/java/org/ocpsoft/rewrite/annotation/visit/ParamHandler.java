package org.ocpsoft.rewrite.annotation.visit;

import org.ocpsoft.rewrite.annotation.api.Parameter;
import org.ocpsoft.rewrite.annotation.api.ParameterContext;
import org.ocpsoft.rewrite.annotation.spi.ParameterAnnotationHandler;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class ParamHandler extends ParameterAnnotationHandler<Param>
{
   @Override
   public Class<Param> handles()
   {
      return Param.class;
   }

   @Override
   public void process(ParameterContext context, Parameter element, Param annotation)
   {
      context.getRuleBuilder()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(201));
   }

}
