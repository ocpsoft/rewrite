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
package org.ocpsoft.rewrite.param;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.ParameterizationException;

/**
 * 
 * A {@link Parameterized} regular expression {@link Pattern}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface ParameterizedPatternBuilder extends ParameterizedPattern
{
   /**
    * Return the {@link ParameterizedPatternParser} corresponding to the pattern with which this
    * {@link ParameterizedPatternBuilder} was constructed.
    */
   ParameterizedPatternParser getParser();

   /**
    * Use this expression to build a {@link String} from this expression's pattern. Extract needed values from the
    * {@link EvaluationContext}.
    * 
    * @throws {@link ParameterizationException} when a required parameter is missing.
    */
   String build(Rewrite event, EvaluationContext context) throws ParameterizationException;

   /**
    * Use this expression to build a {@link String} from this expression's pattern. Extract needed values from the
    * {@link EvaluationContext}. The given {@link Transposition} instances will be performed on each parameter in the
    * order that they were provided.
    * 
    * @throws {@link ParameterizationException} when a required parameter is missing.
    */
   String build(Rewrite event, EvaluationContext context, Transposition<String> transposition)
            throws ParameterizationException;

   /**
    * Use this expression to build a {@link String} from given values.
    * 
    * @throws {@link ParameterizationException} when a required parameter is missing.
    */
   String build(Map<String, Object> parameters) throws ParameterizationException;

   /**
    * Use this expression's pattern to build a {@link String} from the given values. Enforces that the number of values
    * passed must equal the number of expression parameters. Does not apply any configured {@link Transposition}
    * instances.
    * 
    * @throws {@link ParameterizationException} when a required parameter is missing.
    */
   String build(List<Object> values) throws ParameterizationException;

   /**
    * Returns <code>true</code> if all parameters required by this builder are available; otherwise, returns
    * <code>false</code>.
    */
   boolean isParameterComplete(Rewrite event, EvaluationContext context);

}
