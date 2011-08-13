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
package com.ocpsoft.rewrite.servlet.config.bind;

import java.util.List;

import com.ocpsoft.rewrite.bind.BindingBuilder;
import com.ocpsoft.rewrite.bind.Converter;
import com.ocpsoft.rewrite.bind.RetrievalBuilder;
import com.ocpsoft.rewrite.bind.SubmissionBuilder;
import com.ocpsoft.rewrite.bind.Validator;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.exception.RewriteException;
import com.ocpsoft.rewrite.logging.Logger;
import com.ocpsoft.rewrite.services.ServiceLoader;
import com.ocpsoft.rewrite.servlet.spi.ExpressionLanguageProvider;
import com.ocpsoft.rewrite.util.Iterators;

/**
 * Responsible for binding to EL expressions.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class El extends BindingBuilder
{
   private static final Logger log = Logger.getLogger(El.class);
   private static List<ExpressionLanguageProvider> providers;

   /**
    * Create a new EL Method binding using distinct expressions to submit and retrieve values. The method intended for
    * use in submission must accept a single parameter of the expected type.
    */
   public static BindingBuilder methodBinding(final String retrieve, final String submit)
   {
      return new ElMethod(retrieve, submit);
   }

   /**
    * Create a new EL Method binding to retrieve values. The method must return a value of the expected type.
    */
   public static RetrievalBuilder retrievalMethod(final String expression)
   {
      return new ElMethod(expression, null);
   }

   /**
    * Create a new EL Method binding to retrieve values. The method must return a value of the expected type. Use the
    * given {@link Converter} when retrieving any values.
    */
   public static RetrievalBuilder retrievalMethod(final String expression,
            final Class<? extends Converter<?>> converterType)
   {
      ElMethod el = new ElMethod(expression, null);
      el.convertedBy(converterType);
      return el;
   }

   /**
    * Create a new EL Method binding to submit values. The method must accept a single parameter of the expected type.
    */
   public static SubmissionBuilder submissionMethod(final String expression)
   {
      return new ElMethod(null, expression);
   }

   /**
    * Create a new EL Method binding to submit values. The method must accept a single parameter of the expected type.
    * Use the given {@link Converter} before submitting any values.
    */
   public static SubmissionBuilder submissionMethod(final String expression,
            final Class<? extends Converter<?>> converterType)
   {
      ElMethod el = new ElMethod(null, expression);
      el.convertedBy(converterType);
      return el;
   }

   /**
    * Create a new EL Method binding to submit values. The method must accept a single parameter of the expected type.
    * Use the given {@link Validator} before attempting to submit any values. Use the given {@link Converter} before
    * submitting any values.
    */
   public static SubmissionBuilder submissionMethod(final String expression,
            final Class<? extends Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      ElMethod el = new ElMethod(null, expression);
      el.convertedBy(converterType);
      el.validatedBy(validatorType);
      return el;
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter.
    */
   public static BindingBuilder property(final String expression)
   {
      return new ElProperty(expression);
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter. Use the given {@link Converter} before submitting any
    * values.
    */
   public static BindingBuilder property(final String expression, final Class<? extends Converter<?>> type)
   {
      ElProperty el = new ElProperty(expression);
      el.convertedBy(type);
      return el;
   }

   /**
    * Create a new EL Value binding using a single expression to submit and retrieve values. The specified property must
    * either be public, or have a publicly defined getter/setter. Use the given {@link Validator} before attempting to
    * submit any values. Use the given {@link Converter} when submitting any values.
    */
   public static BindingBuilder property(final String expression, final Class<? extends Converter<?>> converterType,
            final Class<? extends Validator<?>> validatorType)
   {
      ElProperty el = new ElProperty(expression);
      el.convertedBy(converterType);
      el.validatedBy(validatorType);
      return el;
   }

   private static List<ExpressionLanguageProvider> getProviders()
   {
      if (providers == null)
      {
         @SuppressWarnings("unchecked")
         ServiceLoader<ExpressionLanguageProvider> serviceProviders = ServiceLoader
                  .load(ExpressionLanguageProvider.class);

         providers = Iterators.asList(serviceProviders.iterator());
      }
      return providers;
   }

   /**
    * Handle EL Method Invocation and Value Extraction
    */
   public static class ElMethod extends El
   {
      private final String getExpression;
      private final String setExpression;

      public ElMethod(final String getExpression, final String setExpression)
      {
         this.getExpression = getExpression;
         this.setExpression = setExpression;
      }

      @Override
      @SuppressWarnings("unchecked")
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         ServiceLoader<ExpressionLanguageProvider> providers = ServiceLoader.load(ExpressionLanguageProvider.class);

         if (!supportsRetrieval())
            throw new RewriteException("Method binding expression supports submission only [" + setExpression
                     + "], no value retrieval expression was defined");

         Object value = null;
         for (ExpressionLanguageProvider provider : providers) {

            try
            {
               value = provider.evaluateMethodExpression(getExpression);
               break;
            }
            catch (Exception e) {
               log.debug("El provider [" + provider.getClass().getName()
                        + "] could not retrieve value from property #{"
                        + getExpression + "}");
            }

            if (value != null)
            {
               break;
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
               return provider.evaluateMethodExpression(setExpression, value);
            }
            catch (Exception e) {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not submit property #{" + setExpression
                        + "} with value [" + value + "]", e);
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
         return "ElMethod [retrieve= #{ " + getExpression + " }, submit= #{ " + setExpression + " }";
      }

   }

   /**
    * Handle EL Property Injection and Extraction
    */
   public static class ElProperty extends El
   {
      private final String expression;

      public ElProperty(final String expression)
      {
         this.expression = expression;
      }

      @Override
      @SuppressWarnings("unchecked")
      public Object retrieve(final Rewrite event, final EvaluationContext context)
      {
         ServiceLoader<ExpressionLanguageProvider> providers = ServiceLoader.load(ExpressionLanguageProvider.class);

         Object value = null;
         for (ExpressionLanguageProvider provider : providers) {

            try
            {
               value = provider.retrieveValue(expression);
               break;
            }
            catch (Exception e) {
               log.debug("El provider [" + provider.getClass().getName() + "] could not extract value from property #{"
                        + expression + "}");
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
         @SuppressWarnings("unchecked")
         ServiceLoader<ExpressionLanguageProvider> providers = ServiceLoader
                  .load(ExpressionLanguageProvider.class);

         if (!providers.iterator().hasNext())
         {
            log.warn("No instances of [{}] were configured. EL support is disabled.",
                     ExpressionLanguageProvider.class.getName());
         }

         for (ExpressionLanguageProvider provider : providers) {
            try
            {
               provider.submitValue(expression, value);
               break;
            }
            catch (Exception e) {
               throw new RewriteException("El provider [" + provider.getClass().getName()
                        + "] could not inject property #{" + expression
                        + "} with value [" + value + "]", e);
            }
         }

         return null;
      }

      @Override
      public String toString()
      {
         return "ElProperty #{ " + expression + " }";
      }

   }
}
