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
package org.ocpsoft.rewrite.el.spi;

import org.ocpsoft.common.pattern.Weighted;


/**
 * Defines a service interface to provide EL support.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ExpressionLanguageProvider extends Weighted
{
   /**
    * Extract a value from the location specified by the given EL location.
    */
   Object retrieveValue(String expression) throws UnsupportedOperationException;

   /**
    * Inject a value into location specified by the given EL expression.
    */
   void submitValue(String expression, Object value) throws UnsupportedOperationException;

   /**
    * Invoke the method specified by the given EL expression, returning the result - if any.
    */
   Object evaluateMethodExpression(String expression) throws UnsupportedOperationException;

   /**
    * Evaluate a method expression, passing the given parameters, returning the result - if any.
    */
   Object evaluateMethodExpression(String expression, Object... values) throws UnsupportedOperationException;
}
