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
package com.ocpsoft.pretty.faces.util;

import java.util.regex.Pattern;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class FacesElUtils
{
   private static final String EL_REGEX = "\\#\\{\\s*[^}]+\\s*\\}";

   /**
    * The pattern to match any EL expression in a String. Regex Group 0 contains
    * the entire expression, no more.
    */
   public static final Pattern elPattern = Pattern.compile(EL_REGEX);

   public Object coerceToType(final FacesContext context, final String expression, final Object value) throws ELException
   {
      ExpressionFactory ef = context.getApplication().getExpressionFactory();
      ValueExpression ve = ef.createValueExpression(context.getELContext(), expression, Object.class);
      return ef.coerceToType(value, ve.getType(context.getELContext()));
   }

   public Class<?> getExpectedType(final FacesContext context, final String expression) throws ELException
   {
      ExpressionFactory ef = context.getApplication().getExpressionFactory();
      ValueExpression ve = ef.createValueExpression(context.getELContext(), expression, Object.class);
      return ve.getType(context.getELContext());
   }

   public Object getValue(final FacesContext context, final String expression) throws ELException
   {
      ExpressionFactory ef = context.getApplication().getExpressionFactory();
      ValueExpression ve = ef.createValueExpression(context.getELContext(), expression, Object.class);
      return ve.getValue(context.getELContext());
   }

   public Object invokeMethod(final FacesContext context, final String expression) throws ELException
   {
      return invokeMethod(context, expression, new Class[] {}, null);
   }

   public Object invokeMethod(final FacesContext context, final String expression, Class<?>[] argumentTypes,
         Object[] argumentValues) throws ELException
   {
      ExpressionFactory ef = context.getApplication().getExpressionFactory();
      MethodExpression me = ef.createMethodExpression(context.getELContext(), expression, Object.class, argumentTypes);
      return me.invoke(context.getELContext(), argumentValues);
   }

   public boolean isEl(final String viewId)
   {
      if (viewId == null)
      {
         return false;
      }
      return elPattern.matcher(viewId).matches();
   }

   public void setValue(final FacesContext context, final String expression, final Object value) throws ELException
   {
      ExpressionFactory ef = context.getApplication().getExpressionFactory();
      ValueExpression ve = ef.createValueExpression(context.getELContext(), expression, Object.class);
      ve.setValue(context.getELContext(), ef.coerceToType(value, ve.getType(context.getELContext())));
   }

   public ValueExpression createValueExpression(final FacesContext context, final String expression) throws ELException
   {
      ExpressionFactory ef = context.getApplication().getExpressionFactory();
      ValueExpression ve = ef.createValueExpression(context.getELContext(), expression, Object.class);
      return ve;
   }
}
