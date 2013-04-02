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
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * A wrapper for an existing operation that will only delegate to the wrapped operation if the current request is a JSF
 * postback. Will only work if the wrapper is deferred.
 * 
 * @author Christian Kaltepoth
 */
public class IgnorePostbackOperation implements Operation
{

   private final Operation delegate;

   public IgnorePostbackOperation(Operation delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {

      FacesContext facesContext = FacesContext.getCurrentInstance();
      Assert.notNull(facesContext, "FacesContext.getCurrentInstance() returned null. " +
               "You should use @Deferred so the operation gets executed within the JSF lifecycle.");

      // getRenderKit() may return null in some situations
      boolean postback = false;
      if (facesContext.getRenderKit() != null) {
         postback = facesContext.getRenderKit().getResponseStateManager().isPostback(facesContext);
      }

      if (!postback) {
         delegate.perform(event, context);
      }

   }

}