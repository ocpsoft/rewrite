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
package org.ocpsoft.rewrite.faces.annotation.config;

import javax.faces.context.FacesContext;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * A wrapper for an existing binding that will only delegate to the wrapped binding if the current request is a JSF
 * postback. Will only work if the wrapper is deferred.
 * 
 * @author Christian Kaltepoth
 */
public class IgnorePostbackBinding implements Binding
{

   private final Binding delegate;

   public IgnorePostbackBinding(Binding delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public Object retrieve(Rewrite event, EvaluationContext context)
   {
      if (!isPostback()) {
         return delegate.retrieve(event, context);
      }
      return null;
   }

   @Override
   public Object submit(Rewrite event, EvaluationContext context, Object value)
   {
      if (!isPostback()) {
         return delegate.submit(event, context, value);
      }
      return null;
   }

   @Override
   public boolean supportsRetrieval()
   {
      return delegate.supportsRetrieval();
   }

   @Override
   public boolean supportsSubmission()
   {
      return delegate.supportsSubmission();
   }

   private boolean isPostback()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
               "You should use @Deferred so the binding gets executed within the JSF lifecycle.");
      return facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext);
   }

}