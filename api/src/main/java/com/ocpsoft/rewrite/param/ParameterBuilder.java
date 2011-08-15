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
package com.ocpsoft.rewrite.param;

import com.ocpsoft.rewrite.bind.DefaultBindable;
import com.ocpsoft.rewrite.bind.Evaluation;

/**
 * An base implementation of {@link Parameter}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterBuilder<P extends ParameterBuilder<P, T>, T> extends DefaultBindable<Parameter<T>>
         implements
         Parameter<T>
{
   private T pattern;

   /**
    * Create a new instance for the given pattern.
    */
   public ParameterBuilder(final T pattern)
   {
      this.pattern = pattern;

      /*
       *  Set up default binding to evaluation context. Use deferred name resolution so 
       *  that we don't need to have a name at construction time.
       */
      this.bindsTo(Evaluation.property(new CharSequence() {

         @Override
         public CharSequence subSequence(final int begin, final int end)
         {
            return getName().subSequence(begin, end);
         }

         @Override
         public int length()
         {
            return getName().length();
         }

         @Override
         public char charAt(final int index)
         {
            return getName().charAt(index);
         }

         @Override
         public String toString()
         {
            return getName();
         }

         @Override
         public boolean equals(final Object obj)
         {
            return getName().equals(obj);
         }

         @Override
         public int hashCode()
         {
            return getName().hashCode();
         }
      }));
   }

   @Override
   @SuppressWarnings("unchecked")
   public P matches(final T pattern)
   {
      this.pattern = pattern;
      return (P) this;
   }

   public T getPattern()
   {
      return pattern;
   }

}
