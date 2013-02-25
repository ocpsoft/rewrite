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
package org.ocpsoft.rewrite.el;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.BindingBuilder;
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
public abstract class El extends BindingBuilder<El, Object> implements Retrieval
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
      return new ElMethod(new TypeBasedExpression(method.getDeclaringClass(), method.getName()), null);
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
      return new ElProperty(new TypeBasedExpression(field.getDeclaringClass(), field.getName()));
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

         Object value = null;
         for (ExpressionLanguageProvider provider : getProviders()) {

            try
            {
               return provider.evaluateMethodExpression(getExpression.getExpression());
            }
            catch (UnsupportedOperationException e) {
               log.debug("El provider [" + provider.getClass().getName()
                        + "] could not invoke method ["
                        + getExpression + "]", e);
            }
            catch (Exception e)
            {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not retrieve value from property ["
                        + getExpression + "]", e);

            }
         }

         return value;
      }

      @Override
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         if (!supportsSubmission())
            throw new RewriteException("Method binding expression supports retrieval only [" + getExpression
                     + "], no value submission expression was defined");

         for (ExpressionLanguageProvider provider : getProviders()) {
            try
            {
               return provider.evaluateMethodExpression(setExpression.getExpression(), value);
            }
            catch (UnsupportedOperationException e) {
               log.debug("El provider [" + provider.getClass().getName()
                        + "] could not submit method [" + setExpression
                        + "} with value [" + value + "]", e);
            }
            catch (Exception e)
            {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not submit method ["
                        + setExpression + "} with value [" + value + "]", e);

            }
         }
         return null;
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
         Object value = null;
         for (ExpressionLanguageProvider provider : getProviders()) {

            try
            {
               value = provider.retrieveValue(expression.getExpression());
               break;
            }
            catch (UnsupportedOperationException e)
            {
               log.debug("El provider [" + provider.getClass().getName() + "] could not extract value from property ["
                        + expression + "]", e);
            }
            catch (Exception e) {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not extract value from property ["
                        + expression + "]", e);
            }

            if (value != null)
            {
               break;
            }
         }

         return value;
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
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         for (ExpressionLanguageProvider provider : getProviders()) {
            try
            {
               provider.submitValue(expression.getExpression(), value);
               break;
            }
            catch (UnsupportedOperationException e)
            {
               log.debug("El provider [" + provider.getClass().getName()
                        + "] could not inject property [" + expression
                        + "} with value [" + value + "]", e);
            }
            catch (Exception e) {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not inject property [" + expression
                        + "} with value [" + value + "]", e);
            }
         }

         return null;
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
         Object value = null;
         for (ExpressionLanguageProvider provider : getProviders()) {

            try
            {
               value = provider.retrieveValue(retrieve.getExpression());
               break;
            }
            catch (UnsupportedOperationException e)
            {
               log.debug("El provider [" + provider.getClass().getName() + "] could not extract value from property ["
                        + retrieve + "]", e);
            }
            catch (Exception e) {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not extract value from property ["
                        + retrieve + "]", e);
            }

            if (value != null)
            {
               break;
            }
         }

         return value;
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
      public Object submit(final Rewrite event, final EvaluationContext context, final Object value)
      {
         for (ExpressionLanguageProvider provider : getProviders()) {
            try
            {
               provider.submitValue(submit.getExpression(), value);
               break;
            }
            catch (UnsupportedOperationException e)
            {
               log.debug("El provider [" + provider.getClass().getName()
                        + "] could not inject property [" + submit
                        + "} with value [" + value + "]", e);
            }
            catch (Exception e) {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not inject property [" + submit
                        + "} with value [" + value + "]", e);
            }
         }

         return null;
      }

      @Override
      public String toString()
      {
         return "ElProperties [ submitTo =>" + submit + ", retrieveFrom=>" + retrieve + " ]";
      }

   }
}
