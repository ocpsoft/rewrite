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
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.inject.Inject;

import org.jboss.seam.solder.el.Expressions;

import com.ocpsoft.rewrite.servlet.spi.ElSupportProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SeamSolderElProvider implements ElSupportProvider
{
   @Inject
   private Expressions expressions;

   @Override
   public Object extractValue(final String expression)
   {
      return getValue(expressions.getELContext(), expressions.getExpressionFactory(),
               expressions.toExpression(expression));
   }

   @Override
   public void injectValue(final String expression, final Object value)
   {
      String el = expressions.toExpression(expression);
      if (getExpectedType(expressions.getELContext(), expressions.getExpressionFactory(), el).isArray())
      {
         Object[] toInject = null;
         if (!value.getClass().isArray())
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
         if (value.getClass().isArray())
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
   public Object invokeMethod(final String expression)
   {
      return invokeMethod(expressions.getELContext(), expressions.getExpressionFactory(),
               expressions.toExpression(expression));
   }

   public Object coerceToType(final ELContext context, final ExpressionFactory factory, final String expression,
            final Object value)
            throws ELException
   {
      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      return factory.coerceToType(value, ve.getType(context));
   }

   public Class<?> getExpectedType(final ELContext context, final ExpressionFactory factory, final String expression)
            throws ELException
   {
      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      return ve.getType(context);
   }

   public Object getValue(final ELContext context, final ExpressionFactory factory, final String expression)
            throws ELException
   {
      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      return ve.getValue(context);
   }

   public Object invokeMethod(final ELContext context, final ExpressionFactory factory, final String expression)
            throws ELException
   {
      return invokeMethod(context, factory, expression, new Class[] {}, null);
   }

   public Object invokeMethod(final ELContext context, final ExpressionFactory factory, final String expression,
            final Class<?>[] argumentTypes,
            final Object[] argumentValues) throws ELException
   {
      MethodExpression me = factory.createMethodExpression(context, expression, Object.class, argumentTypes);
      return me.invoke(context, argumentValues);
   }

   public void setValue(final ELContext context, final ExpressionFactory factory, final String expression,
            final Object value) throws ELException
   {

      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      ve.setValue(context, factory.coerceToType(value, ve.getType(context)));
   }

   public ValueExpression createValueExpression(final ELContext context, final ExpressionFactory factory,
            final String expression) throws ELException
   {
      ValueExpression ve = factory.createValueExpression(context, expression, Object.class);
      return ve;
   }

}
