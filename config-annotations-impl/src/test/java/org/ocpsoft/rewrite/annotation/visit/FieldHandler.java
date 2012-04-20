package org.ocpsoft.rewrite.annotation.visit;

import java.lang.reflect.Field;

import org.ocpsoft.rewrite.annotation.api.FieldContext;
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
   public void process(FieldContext context, Field element, FieldAnno annotation)
   {
      context.getConfigurationBuilder().defineRule()
               .when(Path.matches("/annotation/" + annotation.value()))
               .perform(SendStatus.code(201));
   }

}
