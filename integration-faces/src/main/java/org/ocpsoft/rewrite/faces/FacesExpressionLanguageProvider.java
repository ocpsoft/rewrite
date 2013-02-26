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
package org.ocpsoft.rewrite.faces;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.ocpsoft.rewrite.el.spi.ExpressionLanguageProvider;

/**
 * Implementation of {@link ExpressionLanguageProvider} that uses the {@link FacesContext} to obtain the
 * {@link ELContext} and the {@link ExpressionFactory}.
 * 
 * @author Christian Kaltepoth
 */
public class FacesExpressionLanguageProvider implements ExpressionLanguageProvider
{

   @Override
   public int priority()
   {
      return 30;
   }

   @Override
   public Object retrieveValue(String expression) throws UnsupportedOperationException
   {
      FacesContext facesContext = getFacesContext();
      ELContext elContext = facesContext.getELContext();
      return getValueExpression(facesContext, expression).getValue(elContext);
   }

   @Override
   public void submitValue(String expression, Object value) throws UnsupportedOperationException
   {
      FacesContext facesContext = getFacesContext();
      ELContext elContext = facesContext.getELContext();

      ValueExpression valueExpression = getValueExpression(facesContext, expression);
      Class<?> referencedType = valueExpression.getType(elContext);

      // the value that will be injected
      Object toInject = null;

      // the expression is referencing an array
      if (referencedType.isArray()) {

         // ensure the value that will be injected is an array
         if (value != null && !value.getClass().isArray()) {
            toInject = new Object[] { value };
         }
         else {
            toInject = value;
         }

      }

      // expression is not referencing an array
      else {

         // if value to inject is an array, just use the first element
         if (value != null && value.getClass().isArray()) {
            Object[] valueAsArray = (Object[]) value;
            if (valueAsArray.length > 0) {
               toInject = valueAsArray[0];
            }
            else {
               toInject = null;
            }
         }

         // simple case: neither the expression is referencing an array nor the value is one
         else {
            toInject = value;
         }

      }

      // set the value
      Object coercedValue = facesContext.getApplication().getExpressionFactory().coerceToType(toInject, referencedType);
      valueExpression.setValue(elContext, coercedValue);

   }

   @Override
   public Object evaluateMethodExpression(String expression) throws UnsupportedOperationException
   {
      return evaluateMethodExpression(expression, new Object[0]);
   }

   @Override
   public Object evaluateMethodExpression(String expression, Object... values) throws UnsupportedOperationException
   {
      String el = toELExpression(expression);
      FacesContext facesContext = getFacesContext();
      ELContext elContext = facesContext.getELContext();
      ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
      MethodExpression methodExpression = expressionFactory.createMethodExpression(elContext, el,
               Object.class, new Class[values.length]);
      return methodExpression.invoke(elContext, values);
   }

   /**
    * Creates an {@link ValueExpression} for the supplied EL expression
    */
   private ValueExpression getValueExpression(FacesContext facesContext, String expression)
   {
      String el = toELExpression(expression);
      ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
      return expressionFactory.createValueExpression(facesContext.getELContext(), el, Object.class);
   }

   /**
    * Obtains the {@link FacesContext} using {@link FacesContext#getCurrentInstance()}. This method will throw an
    * {@link IllegalArgumentException} if the {@link FacesContext} is not available.
    */
   private FacesContext getFacesContext()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (facesContext == null) {
         throw new IllegalArgumentException(
                  "FacesContext.getCurrentInstance() returned null. EL expressions can only be evaluated in the JSF lifecycle. "
                           + "You should use PhaseAction and PhaseBinding to perform a deferred operation instead.");
      }
      return facesContext;
   }

   /**
    * Adds #{..} to the expression if required
    */
   private String toELExpression(String s)
   {
      if (s != null && !s.startsWith("#{")) {
         return "#{" + s + "}";
      }
      return s;
   }
}
