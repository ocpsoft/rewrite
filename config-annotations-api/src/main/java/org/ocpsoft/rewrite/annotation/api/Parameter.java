package org.ocpsoft.rewrite.annotation.api;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * {@link AnnotatedElement} for a method parameter.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Parameter extends AnnotatedElement
{
   /**
    * Return the {@link Class} type of this parameter.
    */
   public Class<?> getType();

   /**
    * Return the method declaring this parameter.
    */
   public Method getDeclaringMethod();

   /**
    * Return the index of this parameter in the declaring {@link Method} signature.
    */
   public int getIndex();
}
