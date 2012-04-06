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
package org.ocpsoft.rewrite.bind;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.DefaultConditionBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.exception.RewriteException;

/**
 * Utility class for interacting with {@link Bindable} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class Bindings
{
   /**
    * Submit the given value to all registered {@link Binding} instances of the given {@link Bindable}. Perform this by
    * adding individual {@link BindingOperation} instances via {@link EvaluationContext#addPreOperation(Operation)}
    */
   public static boolean enqueueSubmission(final Rewrite event, final EvaluationContext context,
            final Bindable bindable, final Object value)
   {
      Map<Bindable, Object> map = new LinkedHashMap<Bindable, Object>();
      map.put(bindable, value);
      return enqueuePreOperationSubmissions(event, context, map);
   }

   /**
    * Submit the given value to all registered {@link Binding} instances of all given {@link Bindable} instances.
    * Perform this by adding individual {@link BindingOperation} instances via
    * {@link EvaluationContext#addPreOperation(org.ocpsoft.rewrite.config.Operation)}
    * 
    * @return false if validation fails.
    */
   public static boolean enqueuePreOperationSubmissions(final Rewrite event, final EvaluationContext context,
            final Map<? extends Bindable, ? extends Object> map)
   {
      List<Operation> operations = new ArrayList<Operation>();
      for (Entry<? extends Bindable, ? extends Object> entry : map.entrySet()) {

         Bindable parameter = entry.getKey();
         Object value = entry.getValue();

         List<Binding> bindings = parameter.getBindings();
         for (Binding binding : bindings) {
            try {
               if (binding instanceof Evaluation)
               {
                  /*
                   * Binding to the EvaluationContext is available immediately.
                   */
                  Object convertedValue = binding.convert(event, context, value);
                  if (binding.validate(event, context, convertedValue))
                  {
                     binding.submit(event, context, value);
                  }
                  else
                     return false;
               }
               else
               {
                  Object convertedValue = binding.convert(event, context, value);
                  if (binding.validate(event, context, convertedValue))
                  {
                     convertedValue = binding.convert(event, context, convertedValue);
                     operations.add(new BindingOperation(binding, convertedValue));
                  }
                  else
                     return false;
               }
            }
            catch (Exception e) {
               throw new RewriteException("Failed to bind value [" + value + "] to binding [" + binding + "]", e);
            }
         }
      }

      for (Operation operation : operations) {
         context.addPreOperation(operation);
      }
      return true;
   }

   /**
    * Extract bound values from configured {@link Bindable} instances. Return a {@link List} of the extracted values.
    */
   public static List<Object> performRetrieval(final Rewrite event, final EvaluationContext context,
            final Bindable<?> bindable)
            {
      List<Object> result = new ArrayList<Object>();

      for (Binding binding : bindable.getBindings())
      {
         Object boundValue = binding.retrieve(event, context);
         result.add(boundValue);
      }
      return result;
            }

   /**
    * Return a new {@link Condition} which compares the expected value against the actual retrieved {@link Retrieval}
    * {@link Binding} value. This evaluates to true when the values are equal.
    */
   public static DefaultConditionBuilder equals(final Object expected, final Retrieval binding)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            Object actual = binding.retrieve(event, context);
            return compare(expected, actual);
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the expected value against the actual retrieved {@link Submission}
    * {@link Binding} value. This evaluates to true when the values are equal.
    */
   public static DefaultConditionBuilder equals(final Object expected, final Submission binding, final Object submission)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            Object actual = binding.submit(event, context, submission);
            return compare(expected, actual);
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the value of two {@link Retrieval} {@link Binding} instances. This
    * evaluates to true when the values are equal.
    */
   public static DefaultConditionBuilder equals(final Retrieval left, final Retrieval right)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            return compare(left.retrieve(event, context), right.retrieve(event, context));
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the values of each given {@link Retrieval} and {@link Submission}
    * {@link Binding} instance, respectively. This evaluates to true when the values are equal.
    */
   public static DefaultConditionBuilder equals(final Retrieval left, final Submission right, final Object submission)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            return compare(left.retrieve(event, context), right.submit(event, context, submission));
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the value of two {@link Submission} {@link Binding} instances. This
    * evaluates to true when the values are equal.
    */
   public static DefaultConditionBuilder equals(final Submission left, final Object leftSubmission,
            final Submission right,
            final Object rightSubmission)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            return compare(left.submit(event, context, leftSubmission), right.submit(event, context, rightSubmission));
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the expected value against the actual retrieved {@link Retrieval}
    * {@link Binding} value. This evaluates to true when the values are not equal.
    */
   public static DefaultConditionBuilder notEquals(final Object expected, final Retrieval binding)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            Object actual = binding.retrieve(event, context);
            return !compare(expected, actual);
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the expected value against the actual retrieved {@link Submission}
    * {@link Binding} value. This evaluates to true when the values are not equal.
    */
   public static DefaultConditionBuilder notEquals(final Object expected, final Submission binding,
            final Object submission)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            Object actual = binding.submit(event, context, submission);
            return !compare(expected, actual);
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the value of two {@link Retrieval} {@link Binding} instances. This
    * evaluates to true when the values are not equal.
    */
   public static DefaultConditionBuilder notEquals(final Retrieval left, final Retrieval right)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            return !compare(left.retrieve(event, context), right.retrieve(event, context));
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the values of each given {@link Retrieval} and {@link Submission}
    * {@link Binding} instance, respectively. This evaluates to true when the values are not equal.
    */
   public static DefaultConditionBuilder notEquals(final Retrieval left, final Submission right, final Object submission)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            return !compare(left.retrieve(event, context), right.submit(event, context, submission));
         }
      };
   }

   /**
    * Return a new {@link Condition} which compares the value of two {@link Submission} {@link Binding} instances. This
    * evaluates to true when the values are not equal.
    */
   public static DefaultConditionBuilder notEquals(final Submission left, final Object leftSubmission,
            final Submission right,
            final Object rightSubmission)
   {
      return new DefaultConditionBuilder() {
         @Override
         public boolean evaluate(final Rewrite event, final EvaluationContext context)
         {
            return !compare(left.submit(event, context, leftSubmission), right.submit(event, context, rightSubmission));
         }
      };
   }

   /**
    * Return true if the two values are equal.
    */
   private static boolean compare(final Object expected, final Object actual)
   {
      if (expected == actual)
      {
         return true;
      }
      else if ((expected != null) && expected.equals(actual))
      {
         return true;
      }
      return false;
   }

   /**
    * Used to store bindings until all conditions have been met.
    * 
    * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
    */
   private static class BindingOperation implements Operation
   {
      private final Binding binding;
      private final Object value;

      public BindingOperation(final Binding binding, final Object value)
      {
         this.binding = binding;
         this.value = value;
      }

      @Override
      public void perform(final Rewrite event, final EvaluationContext context)
      {
         binding.submit(event, context, value);
      }

      @Override
      public String toString()
      {
         return "BindingOperation [binding=" + binding + ", value=" + value + "]";
      }

   }
}
