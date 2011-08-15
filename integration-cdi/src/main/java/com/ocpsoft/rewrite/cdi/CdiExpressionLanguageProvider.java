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
package com.ocpsoft.rewrite.cdi;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.inject.Inject;

import org.jboss.seam.solder.el.Expressions;

import com.ocpsoft.rewrite.spi.ExpressionLanguageProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CdiExpressionLanguageProvider implements ExpressionLanguageProvider
{
   @Inject
   private Expressions expressions;

   @Override
   public Object retrieveValue(final String expression)
   {
      return getValue(expressions.getELContext(), expressions.getExpressionFactory(),
               expressions.toExpression(expression));
   }

   @Override
   public void submitValue(final String expression, final Object value)
   {
      String el = expressions.toExpression(expression);
      if (getExpectedType(expressions.getELContext(), expressions.getExpressionFactory(), el).isArray())
      {
         Object[] toInject = null;
         if ((value != null) && !value.getClass().isArray())
         {
            toInject = new Object[] { value };
         }
         else
            toInject = (Object[]) value;

         setValue(expressions.getELContext(), expressions.getExpressionFactory(), el, toInject);
      }
      else
      {
         Object toInject = value;
         if ((value != null) && value.getClass().isArray())
         {
            Object[] array = (Object[]) value;
            if (array.length > 0)
            {
               toInject = array[0];
            }
            else
            {
               toInject = "";
            }
         }
         setValue(expressions.getELContext(), expressions.getExpressionFactory(), el, toInject);
      }
   }

   @Override
   public Object evaluateMethodExpression(final String expression)
   {
      String el = expressions.toExpression(expression);
      return expressions.evaluateMethodExpression(el);
   }

   @Override
   public Object evaluateMethodExpression(final String expression, final Object... values)
   {
      String el = expressions.toExpression(expression);
      return expressions.evaluateMethodExpression(el, values);
   }

   /**
    * Helpers
    */
   private Class<?> getExpectedType(final ELContext context, final ExpressionFactory factory, final String expression)
            throws ELException
   {
      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      return ve.getType(context);
   }

   private Object getValue(final ELContext context, final ExpressionFactory factory, final String expression)
            throws ELException
   {
      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      return ve.getValue(context);
   }

   private void setValue(final ELContext context, final ExpressionFactory factory, final String expression,
            final Object value) throws ELException
   {

      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      ve.setValue(context, factory.coerceToType(value, ve.getType(context)));
   }

}
