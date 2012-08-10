package org.ocpsoft.rewrite.annotation.visit;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.spi.ClassAnnotationHandler;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.SendStatus;

public class TypeHandler extends ClassAnnotationHandler<TypeAnno>
{
   @Override
   public Class<TypeAnno> handles()
   {
      return TypeAnno.class;
   }

   @Override
   public void process(ClassContext context, Class<?> type, TypeAnno annotation)
   {
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
