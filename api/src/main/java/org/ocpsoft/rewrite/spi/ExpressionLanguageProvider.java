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
package org.ocpsoft.rewrite.spi;

import org.ocpsoft.rewrite.exception.UnsupportedEvaluationException;

/**
 * Defines a service interface to provide EL support.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ExpressionLanguageProvider
{
   /**
    * Extract a value from the location specified by the given EL location.
    */
   Object retrieveValue(String expression) throws UnsupportedEvaluationException;

   /**
    * Inject a value into location specified by the given EL expression.
    */
   void submitValue(String expression, Object value) throws UnsupportedEvaluationException;

   /**
    * Invoke the method specified by the given EL expression, returning the result - if any.
    */
   Object evaluateMethodExpression(String expression) throws UnsupportedEvaluationException;

   /**
    * Evaluate a method expression, passing the given parameters, returning the result - if any.
    */
   Object evaluateMethodExpression(String expression, Object... values) throws UnsupportedEvaluationException;
}
