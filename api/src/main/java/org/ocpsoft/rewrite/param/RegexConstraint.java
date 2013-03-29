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
package org.ocpsoft.rewrite.param;

import java.util.regex.Pattern;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Defines a {@link Constraint} using a regular expression.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class RegexConstraint implements Constraint<String>
{
   private final Pattern pattern;

   /**
    * Create a new {@link RegexConstraint} using the given pattern.
    */
   public RegexConstraint(String pattern)
   {
      Assert.notNull(pattern, "Pattern must not be null.");
      this.pattern = Pattern.compile(pattern);
   }

   @Override
   public boolean isSatisfiedBy(Rewrite event, EvaluationContext context, String value)
   {
      return value != null && pattern.matcher(value).matches();
   }

   @Override
   public String toString()
   {
      return getPattern();
   }

   public String getPattern()
   {
      return pattern.pattern();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((pattern.pattern() == null) ? 0 : pattern.pattern().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      RegexConstraint other = (RegexConstraint) obj;
      if (pattern.pattern() == null) {
         if (other.pattern.pattern() != null)
            return false;
      }
      else if (!pattern.pattern().equals(other.pattern.pattern()))
         return false;
      return true;
   }

}
