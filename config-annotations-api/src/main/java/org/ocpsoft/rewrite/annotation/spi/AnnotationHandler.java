package org.ocpsoft.rewrite.annotation.spi;

import java.lang.annotation.Annotation;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;

public interface AnnotationHandler<A extends Annotation> extends Weighted
{
   Class<A> handles();

   void process(ClassContext context, A annotation, HandlerChain chain);
}
