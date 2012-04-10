/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.cdi.expressions;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.inject.Inject;

import org.ocpsoft.rewrite.cdi.util.Reflections;

/**
 * <p>
 * Provides various utility methods for working with EL expressions.
 * </p>
 * <p/>
 * <p>
 * This utility can be used through injection:
 * </p>
 * <p/>
 *
 * <pre>
 * &#064;Inject
 * Expressions expressions;
 * </pre>
 * <p/>
 * <p>
 * Alternatively, if you aren't working in a CDI environment, it can be instantiated using the <code>new</code> keyword:
 * </p>
 * <p/>
 *
 * <pre>
 * Expressions expressions = new Expressions(context, expressionFactory);
 * </pre>
 *
 * @author Pete Muir
 * @author Dan Allen
 * @author Stuart Douglas
 */
public class Expressions
{

   private final ELContext context;
   private final ExpressionFactory expressionFactory;

   /**
    * Create a new instance of the {@link Expressions} class, providing the {@link ELContext} and
    * {@link ExpressionFactory} to be used.
    *
    * @param context the {@link ELContext} against which to operate
    * @param expressionFactory the {@link ExpressionFactory} to use
    * @throws IllegalArgumentException if <code>context</code> is null or <code>expressionFactory</code> is null
    */
   @Inject
   public Expressions(
            @Composite ELContext context, @Composite ExpressionFactory expressionFactory)
   {
      if (context == null) {
         throw new IllegalArgumentException("context must not be null");
      }
      if (expressionFactory == null) {
         throw new IllegalArgumentException("expressionFactory must not be null");
      }
      this.context = context;
      this.expressionFactory = expressionFactory;
   }

   /**
    * Obtain the {@link ELContext} that this instance of {@link Expressions} is using.
    *
    * @return the {@link ELContext} in use
    */
   public ELContext getELContext()
   {
      return context;
   }

   /**
    * Obtain the {@link ExpressionFactory} that this instance of {@link Expressions} is using.
    *
    * @return the {@link ExpressionFactory} in use
    */
   public ExpressionFactory getExpressionFactory()
   {
      return expressionFactory;
   }

   /**
    * <p>
    * Evaluate a {@link ValueExpression}.
    * </p>
    * <p/>
    * <p>
    * A {@link ValueExpression} is created by calling
    * {@link ExpressionFactory#createValueExpression(ELContext, String, Class)} and then
    * {@link ValueExpression#getValue(ELContext)} is called to obtain the value. For more details on the semantics of
    * EL, refer to the javadoc for these classes and methods.
    * </p>
    *
    * @param <T> the type of the evaluated expression
    * @param expression the expression to evaluate
    * @param expectedType the expected type of the evaluated expression
    * @return the result of evaluating the expression
    * @throws NullPointerException if expectedType is <code>null</code>
    * @throws ELException if there are syntactical errors in the provided expression or if an exception was thrown while
    *            performing property or variable resolution. The thrown exception will be included as the cause property
    *            of this exception, if available.
    * @throws PropertyNotFoundException if one of the property resolutions failed because a specified variable or
    *            property does not exist or is not readable.
    * @throws ClassCastException if the result cannot be cast to the expected type
    * @see ExpressionFactory#createValueExpression(ELContext, String, Class)
    * @see ValueExpression#getValue(ELContext)
    */
   public <T> T evaluateValueExpression(String expression, Class<T> expectedType)
   {
      Object result = expressionFactory.createValueExpression(context, expression, expectedType).getValue(context);
      if (result != null) {
         return expectedType.cast(result);
      }
      else {
         return null;
      }
   }

   /**
    * <p>
    * Evaluate a {@link ValueExpression} inferring the return type.
    * </p>
    * <p/>
    * <p>
    * A {@link ValueExpression} is created by calling
    * {@link ExpressionFactory#createValueExpression(ELContext, String, Class)} and then
    * {@link ValueExpression#getValue(ELContext)} is called to obtain the value. For more details on the semantics of
    * EL, refer to the javadoc for these methods.
    * </p>
    *
    * @param <T> the type of the evaluated expression
    * @param expression expression the expression to evaluate
    * @return the result of evaluating the expression
    * @throws ELException if there are syntactical errors in the provided expression
    * @throws ELException if an exception was thrown while performing property or variable resolution. The thrown
    *            exception will be included as the cause property of this exception, if available.
    * @throws PropertyNotFoundException if one of the property resolutions failed because a specified variable or
    *            property does not exist or is not readable
    * @throws ClassCastException if the result cannot be cast to <code>T</code>
    * @see ExpressionFactory#createValueExpression(ELContext, String, Class)
    * @see ValueExpression#getValue(ELContext)
    */
   public <T> T evaluateValueExpression(String expression)
   {
      Object result = evaluateValueExpression(expression, Object.class);
      if (result != null) {
         return Reflections.<T> cast(result);
      }
      else {
         return null;
      }
   }

   /**
    * <p>
    * Evaluate a {@link MethodExpression}, passing arguments and argument types to the method.
    * </p>
    * <p/>
    * <p>
    * A {@link MethodExpression} is created by calling
    * {@link ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])} and then
    * {@link MethodExpression#invoke(ELContext, Object[])} is called to obtain the value. For more details on the
    * semantics of EL, refer to the javadoc for these methods.
    * </p>
    *
    * @param <T> the type of the evaluated expression
    * @param expression expression the expression to evaluate
    * @param expectedReturnType the expected return type of the evaluated expression
    * @param params arguments to the method
    * @param expectedParamTypes of the arguments to the method
    * @return the result of evaluating the expression
    * @throws ClassCastException if the result cannot be cast to <code>expectedReturnType</code>
    * @throws ELException if there are syntactical errors in the provided expression.
    * @throws NullPointerException if <code>expectedParamTypes</code> is <code>null</code>.
    * @throws PropertyNotFoundException if one of the property resolutions failed because a specified variable or
    *            property does not exist or is not readable.
    * @throws MethodNotFoundException if no suitable method can be found.
    * @throws ELException if a String literal is specified and expectedReturnType of the MethodExpression is void or if
    *            the coercion of the String literal to the expectedReturnType yields an error (see Section
    *            "1.18 Type Conversion").
    * @throws ELException if an exception was thrown while performing property or variable resolution. The thrown
    *            exception must be included as the cause property of this exception, if available. If the exception
    *            thrown is an <code>InvocationTargetException</code>, extract its <code>cause</code> and pass it to the
    *            <code>ELException</code> constructor.
    * @see MethodExpression#invoke(ELContext, Object[])
    * @see ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])
    */
   public <T> T evaluateMethodExpression(String expression, Class<T> expectedReturnType, Object[] params,
            Class<?>[] expectedParamTypes)
   {
      Object result = expressionFactory.createMethodExpression(context, expression, expectedReturnType,
               expectedParamTypes).invoke(context, params);
      if (result != null) {
         return expectedReturnType.cast(result);
      }
      else {
         return null;
      }
   }

   /**
    * <p>
    * Evaluate a {@link MethodExpression} with no parameters.
    * </p>
    * <p/>
    * <p>
    * A {@link MethodExpression} is created by calling
    * {@link ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])} and then
    * {@link MethodExpression#invoke(ELContext, Object[])} is called to obtain the value. For more details on the
    * semantics of EL, refer to the javadoc for these methods.
    * </p>
    *
    * @param <T> the type of the evaluated expression
    * @param expression expression the expression to evaluate
    * @param expectedReturnType the expected return type of the evaluated expression
    * @return the result of evaluating the expression
    * @throws ClassCastException if the result cannot be cast to <code>expectedReturnType</code>
    * @throws ELException if there are syntactical errors in the provided expression.
    * @throws NullPointerException if <code>expectedParamTypes</code> is <code>null</code>.
    * @throws PropertyNotFoundException if one of the property resolutions failed because a specified variable or
    *            property does not exist or is not readable.
    * @throws MethodNotFoundException if no suitable method can be found.
    * @throws ELException if a String literal is specified and expectedReturnType of the MethodExpression is void or if
    *            the coercion of the String literal to the expectedReturnType yields an error (see Section
    *            "1.18 Type Conversion").
    * @throws ELException if an exception was thrown while performing property or variable resolution. The thrown
    *            exception must be included as the cause property of this exception, if available. If the exception
    *            thrown is an <code>InvocationTargetException</code>, extract its <code>cause</code> and pass it to the
    *            <code>ELException</code> constructor.
    * @see MethodExpression#invoke(ELContext, Object[])
    * @see ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])
    */
   public <T> T evaluateMethodExpression(String expression, Class<T> expectedReturnType)
   {
      return evaluateMethodExpression(expression, expectedReturnType, new Object[0], new Class[0]);
   }

   /**
    * <p>
    * Evaluate a {@link MethodExpression} with no parameters, inferring the return type.
    * </p>
    * <p/>
    * <p>
    * A {@link MethodExpression} is created by calling
    * {@link ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])} and then
    * {@link MethodExpression#invoke(ELContext, Object[])} is called to obtain the value. For more details on the
    * semantics of EL, refer to the javadoc for these methods.
    * </p>
    *
    * @param <T> the type of the evaluated expression
    * @param expression expression the expression to evaluate
    * @return the result of evaluating the expression
    * @throws ClassCastException if the result cannot be cast to <code>T</code>
    * @throws ELException if there are syntactical errors in the provided expression.
    * @throws NullPointerException if <code>expectedParamTypes</code> is <code>null</code>.
    * @throws PropertyNotFoundException if one of the property resolutions failed because a specified variable or
    *            property does not exist or is not readable.
    * @throws MethodNotFoundException if no suitable method can be found.
    * @throws ELException if a String literal is specified and expectedReturnType of the MethodExpression is void or if
    *            the coercion of the String literal to the expectedReturnType yields an error (see Section
    *            "1.18 Type Conversion").
    * @throws ELException if an exception was thrown while performing property or variable resolution. The thrown
    *            exception must be included as the cause property of this exception, if available. If the exception
    *            thrown is an <code>InvocationTargetException</code>, extract its <code>cause</code> and pass it to the
    *            <code>ELException</code> constructor.
    * @see MethodExpression#invoke(ELContext, Object[])
    * @see ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])
    */
   public <T> T evaluateMethodExpression(String expression)
   {
      Object result = evaluateMethodExpression(expression, Object.class);
      if (result != null) {
         return Reflections.<T> cast(result);
      }
      else {
         return null;
      }
   }

   /**
    * <p>
    * Evaluate a {@link MethodExpression}, passing arguments to the method. The types of the arguments are discoverted
    * from the arguments, and the return type is inferred.
    * </p>
    * <p/>
    * <p>
    * A {@link MethodExpression} is created by calling
    * {@link ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])} and then
    * {@link MethodExpression#invoke(ELContext, Object[])} is called to obtain the value. For more details on the
    * semantics of EL, refer to the javadoc for these methods.
    * </p>
    *
    * @param <T> the type of the evaluated expression
    * @param expression expression the expression to evaluate
    * @param params arguments to the method
    * @return the result of evaluating the expression
    * @throws ClassCastException if the result cannot be cast to <code>T</code>
    * @throws ELException if there are syntactical errors in the provided expression.
    * @throws NullPointerException if <code>expectedParamTypes</code> is <code>null</code>.
    * @throws PropertyNotFoundException if one of the property resolutions failed because a specified variable or
    *            property does not exist or is not readable.
    * @throws MethodNotFoundException if no suitable method can be found.
    * @throws ELException if a String literal is specified and expectedReturnType of the MethodExpression is void or if
    *            the coercion of the String literal to the expectedReturnType yields an error (see Section
    *            "1.18 Type Conversion").
    * @throws ELException if an exception was thrown while performing property or variable resolution. The thrown
    *            exception must be included as the cause property of this exception, if available. If the exception
    *            thrown is an <code>InvocationTargetException</code>, extract its <code>cause</code> and pass it to the
    *            <code>ELException</code> constructor.
    * @see MethodExpression#invoke(ELContext, Object[])
    * @see ExpressionFactory#createMethodExpression(ELContext, String, Class, Class[])
    */
   public <T> T evaluateMethodExpression(String expression, Object... params)
   {
      Object result = evaluateMethodExpression(expression, Object.class, params, new Class[params.length]);
      if (result != null) {
         return Reflections.<T> cast(result);
      }
      else {
         return null;
      }
   }

   /**
    * Convert's a bean name to an EL expression string.
    *
    * @param name the name of the bean to convert
    * @return the expression string
    */
   public String toExpression(String name)
   {
      return "#{" + name + "}";
   }
}
