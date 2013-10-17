/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.el;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Retrieval;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.el.spi.ExpressionLanguageProvider;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;

/**
 * Responsible for binding to EL expressions.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class El implements Binding, Retrieval
{
   private static final Logger log = Logger.getLogger(El.class);
   private static volatile List<ExpressionLanguageProvider> _providers;

   /**
    * Create a new EL Method binding using distinct expressions to submit and retrieve values. The method intended for
    * use in submission must accept a single parameter of the expected type.
    */
   public static El method(final String retrieve, final String submit)
   {
      return new ElMethod(new ConstantExpression(retrieve), new ConstantExpression(submit));
   }

   /**
    * Create a new EL Method binding to retrieve values. The method must return a value of the expected type.
    */
   public static El retrievalMethod(final String expression)
   {
      return new ElMethod(new ConstantExpression(expression), null);
   }

   /**
    * Create a new EL Method binding to retrieve values. This method allows the caller to supply {@link Method} instance
    * to refer to the method that should be invoked.
    */
   public static El retrievalMethod(final Method method)
   {
      return retrievalMethod(method.getDeclaringClass(), method.getName());
   }

   /**
    * Create a new EL Method binding to retrieve values. This method allows the caller to supply the {@link Class}
    * representing the type of the target object and the method name to refer to the method that should be invoked.
    */
   public static El retrievalMethod(final Class<?> clazz, final String methodName)
   {
      return new ElMethod(new TypeBasedExpression(clazz, methodName), null);
   }

   /**
    * Create a new EL Method binding to submit values. The method must accept a single parameter of the expected type.
    */
   public static El submissionMethod(final String expression)
   {
      return new ElMethod(null, new ConstantExpression(expression));
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter.
    */
   public static El property(final String expression)
   {
      return new ElProperty(new ConstantExpression(expression));
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter. Instead of an EL expression this method expects a
    * {@link Field} argument. The EL expression will be automatically created at runtime.
    */
   public static El property(final Field field)
   {
      return property(field.getDeclaringClass(), field.getName());
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter. Instead of an EL expression this method expects the
    * {@link Class} representing the type of the target object and the field name. The EL expression will be
    * automatically created at runtime.
    */
   public static El property(final Class<?> clazz, final String fieldName)
   {
      return new ElProperty(new TypeBasedExpression(clazz, fieldName));
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter.
    */
   public static El properties(final String submit, final String retrieve)
   {
      return new ElProperties(new ConstantExpression(submit),
               new ConstantExpression(retrieve));
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter. Instead of an EL expression this method expects a
    * {@link Field} argument. The EL expression will be automatically created at runtime.
    */
   public static El properties(final Field submit, final Field retrieve)
   {
      return new ElProperties(new TypeBasedExpression(submit.getDeclaringClass(), submit.getName()),
               new TypeBasedExpression(retrieve.getDeclaringClass(), retrieve.getName()));
   }

   @SuppressWarnings("unchecked")
   private static List<ExpressionLanguageProvider> getProviders()
   {
      if (_providers == null)
      {
         synchronized (El.class)
         {
            if (_providers == null)
            {
               _providers = Iterators.asList(ServiceLoader.load(ExpressionLanguageProvider.class));
               Collections.sort(_providers, new WeightedComparator());

               if (_providers.isEmpty())
               {
                  log.warn("No instances of [{}] were configured. EL support is disabled.",
                           ExpressionLanguageProvider.class.getName());
               }

            }

         }
      }
      return _providers;
   }

   private static Object executeProviderCallable(Rewrite event, EvaluationContext context,
            ProviderCallable<Object> providerCallable)
   {
      List<Exception> exceptions = new ArrayList<Exception>();
      for (ExpressionLanguageProvider provider : getProviders()) {
         try
         {
            return providerCallable.call(event, context, provider);
         }
         catch (RuntimeException e) {
            throw e;
         }
         catch (Exception e)
         {
            exceptions.add(e);
         }
      }

      for (Exception exception : exceptions) {
         log.error("DEFERRED EXCEPTION", exception);
      }
      throw new RewriteException("No registered " + ExpressionLanguageProvider.class.getName()
               + " could handle the Expression [" + providerCallable.getExpression() + "]");
   }

   /**
    * Handle EL Method Invocation and Value Extraction
    */
   public static class ElMethod extends El
   {
      private final Expression getExpression;
      private final Expression setExpression;

      public ElMethod(final Expression getExpression, final Expression setExpression)
      {
         this.getExpression = getExpression;
         this.setExpression = setExpression;
      }

      @Override
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         if (!supportsRetrieval())
            throw new RewriteException("Method binding expression supports submission only [" + setExpression
                     + "], no value retrieval expression was defined");

         return executeProviderCallable(event, context, new ProviderCallable<Object>() {
            @Override
            public Object call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider)
                     throws Exception
            {
               return provider.evaluateMethodExpression(getExpression.getExpression());
            }

            @Override
            public String getExpression()
            {
               return ElMethod.this.getExpression.getExpression();
            }
         });
      }

      @Override
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         if (!supportsSubmission())
            throw new RewriteException("Method binding expression supports retrieval only [" + getExpression
                     + "], no value submission expression was defined");

         return executeProviderCallable(event, context, new ProviderCallable<Object>() {
            @Override
            public Object call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider)
                     throws Exception
            {
               return provider.evaluateMethodExpression(setExpression.getExpression(), value);
            }

            @Override
            public String getExpression()
            {
               return ElMethod.this.setExpression.getExpression();
            }
         });
      }

      @Override
      public boolean supportsRetrieval()
      {
         return getExpression != null;
      }

      @Override
      public boolean supportsSubmission()
      {
         return setExpression != null;
      }

      @Override
      public String toString()
      {
         return "ElMethod [retrieve= [ " + getExpression + " }, submit= [ " + setExpression + " ]";
      }

   }

   /**
    * Handle EL Property Injection and Extraction
    */
   public static class ElProperty extends El
   {
      private final Expression expression;

      public ElProperty(final Expression expression)
      {
         this.expression = expression;
      }

      @Override
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         return executeProviderCallable(event, context, new ProviderCallable<Object>() {
            @Override
            public Object call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider)
                     throws Exception
            {
               return provider.retrieveValue(expression.getExpression());
            }

            @Override
            public String getExpression()
            {
               return ElProperty.this.expression.getExpression();
            }
         });
      }

      @Override
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         return executeProviderCallable(event, context, new ProviderCallable<Object>() {
            @Override
            public Object call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider)
                     throws Exception
            {
               provider.submitValue(expression.getExpression(), value);
               return null;
            }

            @Override
            public String getExpression()
            {
               return ElProperty.this.expression.getExpression();
            }
         });
      }

      @Override
      public boolean supportsRetrieval()
      {
         return true;
      }

      @Override
      public boolean supportsSubmission()
      {
         return true;
      }

      @Override
      public String toString()
      {
         return "ElProperty [ " + expression + " ]";
      }

   }

   public static class ElProperties extends El
   {

      private Expression submit;
      private Expression retrieve;

      public ElProperties(final Expression submit, final Expression retrieve)
      {
         this.submit = submit;
         this.retrieve = retrieve;
      }

      @Override
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         return executeProviderCallable(event, context, new ProviderCallable<Object>() {
            @Override
            public Object call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider)
                     throws Exception
            {
               return provider.retrieveValue(retrieve.getExpression());
            }

            @Override
            public String getExpression()
            {
               return retrieve.getExpression();
            }
         });
      }

      @Override
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         return executeProviderCallable(event, context, new ProviderCallable<Object>() {
            @Override
            public Object call(Rewrite event, EvaluationContext context, ExpressionLanguageProvider provider)
                     throws Exception
            {
               provider.submitValue(submit.getExpression(), value);
               return null;
            }

            @Override
            public String getExpression()
            {
               return submit.getExpression();
            }
         });
      }

      @Override
      public boolean supportsRetrieval()
      {
         return true;
      }

      @Override
      public boolean supportsSubmission()
      {
         return true;
      }

      @Override
      public String toString()
      {
         return "ElProperties [ submitTo =>" + submit + ", retrieveFrom=>" + retrieve + " ]";
      }

   }
}
