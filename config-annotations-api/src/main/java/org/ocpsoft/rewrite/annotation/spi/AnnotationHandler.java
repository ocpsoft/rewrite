package org.ocpsoft.rewrite.annotation.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.annotation.api.ClassContext;

public interface AnnotationHandler<A extends Annotation> extends Weighted
{
   Class<A> handles();

   void process(ClassContext context, AnnotatedElement element, A annotation);
}
