package org.ocpsoft.rewrite.annotation.visit;

import org.ocpsoft.rewrite.annotation.api.Parameter;
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
   public void process(ParameterContext context, Parameter element, ParamAnno annotation)
   {
      context.getConfigurationBuilder().addRule()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(203));
   }

}
