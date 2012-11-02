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

/**
 * Implementation of {@link PrettyExpression} used when the EL expression is
 * known in advance.
 * 
 * @author Christian Kaltepoth
 */
public class ConstantExpression implements PrettyExpression
{

   /**
    * Holds the expression
    */
   private final String expression;

   /**
    * Creates a new {@link ConstantExpression}.
    * 
    * @param expression
    *            The EL expressions
    */
   public ConstantExpression(String expression)
   {
      this.expression = expression;
   }

   /*
    * @see com.ocpsoft.pretty.expression.PrettyExpression#getELExpression()
    */
   public String getELExpression()
   {
      return expression;
   }

   @Override
   public String toString()
   {
      return expression;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((expression == null) ? 0 : expression.hashCode());
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
      ConstantExpression other = (ConstantExpression) obj;
      if (expression == null)
      {
         if (other.expression != null)
            return false;
      }
      else if (!expression.equals(other.expression))
         return false;
      return true;
   }

}
