package org.ocpsoft.rewrite.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;

public class HandlerChainImpl implements HandlerChain
{

   private final ClassContext context;

   private AnnotatedElement element;

   private final List<AnnotationHandler<Annotation>> handlers;

   private int pos = 0;

   @SuppressWarnings("unchecked")
   public HandlerChainImpl(ClassContext context, AnnotatedElement element, List<? extends AnnotationHandler<?>> handlers)
   {
      this.context = context;
      this.element = element;
      this.handlers = new ArrayList<AnnotationHandler<Annotation>>();
      this.handlers.addAll((Collection<? extends AnnotationHandler<Annotation>>) handlers);
   }

   @Override
   public void proceed()
   {
      if (pos < handlers.size()) {
         AnnotationHandler<Annotation> handler = handlers.get(pos++);
         Annotation annotation = element.getAnnotation(handler.handles());
         Assert.notNull(annotation, "Could not find annotation [" + handler.handles().getName() + "] on: " + element);
         handler.process(context, annotation, this);
      }

   }

}
