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
package com.ocpsoft.pretty.faces.el;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.el.processor.Injected;
import com.ocpsoft.pretty.faces.el.processor.Named;
import com.ocpsoft.pretty.faces.el.processor.NamedInjected;
import com.ocpsoft.pretty.faces.el.processor.PathParameterProcessor;
import com.ocpsoft.pretty.faces.el.processor.PlainText;
import com.ocpsoft.pretty.faces.el.processor.RegexOverride;

public abstract class ExpressionProcessorRunner
{
   public static final List<PathParameterProcessor> processors;
   public static final List<PathParameterProcessor> preProcessors;

   static
   {
      List<PathParameterProcessor> temp = new ArrayList<PathParameterProcessor>();

      temp.add(new Named());
      temp.add(new NamedInjected());
      temp.add(new Injected());
      temp.add(new PlainText());

      processors = Collections.unmodifiableList(temp);

      temp = new ArrayList<PathParameterProcessor>();
      temp.add(new RegexOverride());

      preProcessors = Collections.unmodifiableList(temp);
   }

   public static PathParameter process(final String expression)
   {
      PathParameter result = new PathParameter();
      result.setExpression(new ConstantExpression(expression));

      for (PathParameterProcessor p : preProcessors)
      {
         result = p.process(result);
      }

      for (PathParameterProcessor p : processors)
      {
         result = p.process(result);
      }

      if (result == null)
      {
         throw new PrettyException("Malformed EL expression: " + expression + ", discovered.");
      }

      return result;
   }

   // public static PathParameter preprocess(final String expression)
   // {
   // PathParameter result = new PathParameter();
   // result.setExpression(expression);
   //
   // for (PathParameterProcessor p : preProcessors)
   // {
   // result = p.process(result);
   // }
   //
   // if (result == null)
   // {
   // throw new PrettyException("Malformed EL expression: " + expression +
   // ", discovered.");
   // }
   //
   // return result;
   // }

}
