/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.spi;

import java.util.List;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.ConfigurationRuleBuilder;
import org.ocpsoft.rewrite.config.Operation;

/**
 * Intercepts values passed to the methods of {@link ConfigurationBuilder} when defining rules.
 * 
 * Additional interceptors may be specified by providing a service activator file containing the name of your
 * implementations:
 * <p>
 * <code> /META-INF/services/org.ocpsoft.rewrite.config.ConfigurationBuilderInterceptor<br>
 * 
 * --------------<br>
 * com.example.ConfigurationBuilderInterceptorImpl</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ConfigurationRuleBuilderInterceptor extends Weighted
{
   /**
    * Intercept, modify, or replace the given {@link Condition}
    * 
    * @see ConfigurationRuleBuilder#when(Condition)
    */
   Condition when(Condition condition);

   /**
    * Intercept, modify, or replace the given {@link Condition} instances
    * 
    * @see ConfigurationRuleBuilder#when(Condition, Condition...)
    */
   List<Condition> when(List<Condition> list);

   /**
    * Intercept, modify, or replace the given {@link Operation}
    * 
    * @see ConfigurationRuleBuilder#perform(Operation)
    */
   Operation perform(Operation operation);

   /**
    * Intercept, modify, or replace the given {@link Operation} instances
    * 
    * @see ConfigurationRuleBuilder#perform(Operation, Operation...)
    */
   List<Operation> perform(List<Operation> list);

   /**
    * Intercept, modify, or replace the given {@link Operation}
    * 
    * @see ConfigurationRuleBuilder#otherwise(Operation)
    */
   Operation otherwise(Operation operation);

   /**
    * Intercept, modify, or replace the given {@link Operation} instances
    * 
    * @see ConfigurationRuleBuilder#otherwise(Operation)
    */
   List<Operation> otherwise(List<Operation> list);

   /**
    * Intercept, modify, or replace the given priority
    * 
    * @see ConfigurationRuleBuilder#withPriority(int)
    */
   int withPriority(int priority);

   /**
    * Intercept, modify, or replace the given ID
    * 
    * @see ConfigurationRuleBuilder#withId(String)
    */
   String withId(String id);
}
