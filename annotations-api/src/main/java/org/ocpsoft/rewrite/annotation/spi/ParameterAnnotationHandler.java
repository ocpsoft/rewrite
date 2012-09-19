/*
 * Copyright 2010 Lincoln Baxter, III
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.annotation.spi;

import java.lang.annotation.Annotation;

import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.api.ParameterContext;

/**
 * A common base class for {@link AnnotationHandler} implementations that process method argument annotations.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterAnnotationHandler<A extends Annotation> implements AnnotationHandler<A>
{

   @Override
   public final void process(ClassContext context, A annotation, HandlerChain chain)
   {
      if (context instanceof ParameterContext) {
         process((ParameterContext) context, annotation, chain);
      }
   }

   public abstract void process(ParameterContext context, A annotation, HandlerChain chain);

}
