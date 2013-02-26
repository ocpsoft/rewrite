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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
public abstract class Bindings
{
   /**
    * Submit the given value to all registered {@link Binding} instances of the given {@link HasBindings}. Perform this
    * by adding individual {@link BindingOperation} instances via {@link EvaluationContext#addPreOperation(Operation)}
    */
   public static boolean enqueueSubmission(final Rewrite event, final EvaluationContext context,
            final HasBindings bindable, final Object value)
   {
      if (value == null)
         return true;

      Map<HasBindings, Object> map = new LinkedHashMap<HasBindings, Object>();
      map.put(bindable, value);

      List<Operation> operations = new ArrayList<Operation>();
      List<Binding> bindings = bindable.getBindings();
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

      for (Operation operation : operations) {
         context.addPreOperation(operation);
      }

      return true;
   }

   /**
    * Extract bound values from configured {@link Bindable} instances. Return a {@link List} of the extracted values.
    */
   public static Object performRetrieval(final Rewrite event, final EvaluationContext context,
            final HasBindings bindable)
   {
      Object result = null;

      List<Binding> bindings = new ArrayList<Binding>(bindable.getBindings());
      Collections.reverse(bindings);
      // FIXME Should not have to worry about order here.

      for (Binding binding : bindings)
      {
         if (result == null && !(binding instanceof Evaluation))
         {
            result = binding.retrieve(event, context);
         }
      }

      for (Binding binding : bindings)
      {
         if (binding instanceof Evaluation)
         {
            if (((Evaluation) binding).hasValue(event, context))
            {
               result = binding.retrieve(event, context);
            }
         }
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
