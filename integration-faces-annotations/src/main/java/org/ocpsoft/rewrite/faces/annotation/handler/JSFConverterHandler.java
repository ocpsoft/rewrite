/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.faces.annotation.handler;

import java.lang.reflect.Field;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.handler.HandlerWeights;
import org.ocpsoft.rewrite.annotation.spi.FieldAnnotationHandler;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.BindingBuilder;
import org.ocpsoft.rewrite.faces.annotation.JSFConverter;
import org.ocpsoft.rewrite.faces.annotation.config.FacesConverterAdapter;

/**
 * Handler for {@link JSFConverter} annotation.
 * 
 * @author Christian Kaltepoth
 */
public class JSFConverterHandler extends FieldAnnotationHandler<JSFConverter>
{

   private final Logger log = Logger.getLogger(JSFConverterHandler.class);

   @Override
   public Class<JSFConverter> handles()
   {
      return JSFConverter.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_ENRICHING;
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void process(FieldContext context, JSFConverter annotation, HandlerChain chain)
   {

      Field field = context.getJavaField();

      // locate the binding previously created by @ParameterBinding
      Binding binding = (Binding) context.get(Binding.class);
      if (binding != null) {

         Assert.assertTrue(binding instanceof BindingBuilder,
                  "Found Binding which is not a BindingBuilder but: " + binding.getClass().getSimpleName());
         BindingBuilder bindingBuilder = (BindingBuilder) binding;

         // add the converter (either by ID or by type)
         if (annotation.value().length() > 0) {
            bindingBuilder.convertedBy(new FacesConverterAdapter<Object>(annotation.value()));
         }
         else {
            bindingBuilder.convertedBy(new FacesConverterAdapter<Object>(field.getType()));
         }

         // some logging
         if (log.isTraceEnabled()) {
            log.trace("Attached JSF converter adapter to field [{}] of class [{}]", new Object[] {
                     bindingBuilder.getConverter(), field.getName(), field.getDeclaringClass().getName()
            });
         }
      }

      // continue with the chain
      chain.proceed();

   }
}