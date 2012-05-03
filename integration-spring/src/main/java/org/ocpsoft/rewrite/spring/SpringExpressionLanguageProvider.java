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
package org.ocpsoft.rewrite.spring;

import org.ocpsoft.rewrite.el.spi.ExpressionLanguageProvider;
import org.ocpsoft.rewrite.exception.UnsupportedEvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.web.context.WebApplicationContext;

/**
 * Implementation of {@link org.ocpsoft.rewrite.el.spi.ExpressionLanguageProvider} for the Spring Expression Language
 * (SpEL)
 * 
 * @author Christian Kaltepoth
 */
public class SpringExpressionLanguageProvider implements ExpressionLanguageProvider
{

   /**
    * Used to parse the SpEL expressions
    */
   private final ExpressionParser parser = new SpelExpressionParser();

   @Autowired
   private WebApplicationContext applicationContext;

   @Override
   public Object retrieveValue(String expression) throws UnsupportedEvaluationException
   {

      try {

         Expression exp = parser.parseExpression(expression);
         return exp.getValue(getEvaluationContext());

      }
      catch (SpelEvaluationException e) {
         throw new IllegalArgumentException(e);
      }

   }

   @Override
   public void submitValue(String expression, Object value) throws UnsupportedEvaluationException
   {

      try {

         Expression exp = parser.parseExpression(expression);
         exp.setValue(getEvaluationContext(), value);

      }
      catch (SpelEvaluationException e) {
         throw new IllegalArgumentException(e);
      }

   }

   @Override
   public Object evaluateMethodExpression(String expression) throws UnsupportedEvaluationException
   {

      try {

         // the method expression MUST end with ()
         String el = expression;
         if (!el.endsWith("()")) {
            el = el + "()";
         }

         // evaluate the expression
         Expression exp = parser.parseExpression(el);
         return exp.getValue(getEvaluationContext());

      }
      catch (SpelEvaluationException e) {
         throw new UnsupportedEvaluationException(e);
      }

   }

   @Override
   public Object evaluateMethodExpression(String expression, Object... values) throws UnsupportedEvaluationException
   {
      throw new UnsupportedEvaluationException();
   }

   /**
    * Lazily initialized by {@link #getEvaluationContext()}
    */
   private EvaluationContext _evaluationContext = null;

   /**
    * Lazily creates a StandardEvaluationContext. The code has been inspired by
    * {@link StandardBeanExpressionResolver#evaluate(String, BeanExpressionContext)}
    */
   public EvaluationContext getEvaluationContext()
   {

      if (_evaluationContext == null) {

         // we need a ConfigurableBeanFactory to build the BeanExpressionContext
         ConfigurableBeanFactory beanFactory = null;

         // the WebApplicationContext MAY implement ConfigurableBeanFactory
         if (applicationContext instanceof ConfigurableBeanFactory) {
            beanFactory = (ConfigurableBeanFactory) applicationContext;
         }

         // the AutowireCapableBeanFactory usually implements ConfigurableListableBeanFactory
         if (beanFactory == null && applicationContext != null
                  && applicationContext.getAutowireCapableBeanFactory() instanceof ConfigurableBeanFactory) {
            beanFactory = (ConfigurableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
         }

         // we cannot continue without a ConfigurableBeanFactory
         if (beanFactory == null) {
            throw new IllegalStateException("Unable to find a ConfigurableBeanFactory");
         }

         BeanExpressionContext beanEvaluationContext = new BeanExpressionContext(beanFactory, null);

         StandardEvaluationContext sec = new StandardEvaluationContext();
         sec.setRootObject(beanEvaluationContext);
         sec.addPropertyAccessor(new BeanExpressionContextAccessor());
         sec.addPropertyAccessor(new BeanFactoryAccessor());
         sec.addPropertyAccessor(new MapAccessor());
         sec.setBeanResolver(new BeanFactoryResolver(beanEvaluationContext.getBeanFactory()));
         sec.setTypeLocator(new StandardTypeLocator(beanEvaluationContext.getBeanFactory().getBeanClassLoader()));
         ConversionService conversionService = beanEvaluationContext.getBeanFactory().getConversionService();
         if (conversionService != null) {
            sec.setTypeConverter(new StandardTypeConverter(conversionService));
         }

         _evaluationContext = sec;
      }

      return _evaluationContext;

   }

}
