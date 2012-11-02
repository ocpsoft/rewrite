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
package com.ocpsoft.pretty.faces.config.mapping;

import com.ocpsoft.pretty.faces.el.PrettyExpression;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class PathParameter extends RequestParameter
{
   private static final String PATH_PARAM_NAME_PREFIX = "com.ocpsoft.vP_";
   private static final String DEFAULT_PATH_REGEX = "[^/]+";

   private int position;
   private String regex = DEFAULT_PATH_REGEX;
   private boolean expressionIsPlainText = true;

   public PathParameter()
   {
      super();
   }

   public PathParameter(final String name, final String value, final PrettyExpression expression)
   {
      super(name, value, expression);
   }

   public PathParameter(final String name, final String value)
   {
      super(name, value);
   }

   public PathParameter copy()
   {
      PathParameter result = new PathParameter();
      if (isNamed())
      {
         result.setName(getName());
      }
      result.setName(getName());
      result.setValue(getValue());
      result.setPosition(getPosition());
      result.setExpression(getExpression());
      result.setRegex(getRegex());
      result.setExpressionIsPlainText(expressionIsPlainText());
      return result;
   }

   public int getPosition()
   {
      return position;
   }

   public void setPosition(final int param)
   {
      position = param;
   }

   public boolean isNamed()
   {
      return (null != super.getName()) && !"".equals(super.getName().trim());
   }

   @Override
   public String getName()
   {
      if (!isNamed())
      {
         return PATH_PARAM_NAME_PREFIX + getPosition();
      }
      return super.getName();
   }

   public String getRegex()
   {
      return regex;
   }

   public void setRegex(final String regex)
   {
      this.regex = regex;
   }

   public void setExpressionIsPlainText(final boolean value)
   {
      this.expressionIsPlainText = value;
   }

   public boolean expressionIsPlainText()
   {
      return expressionIsPlainText;
   }

   @Override
   public String toString()
   {
      return "PathParameter [position=" + position + ", regex=" + regex + ", name=" + getName() + ", expression=" + getExpression() + ", value=" + getValue() + "]";
   }
}
