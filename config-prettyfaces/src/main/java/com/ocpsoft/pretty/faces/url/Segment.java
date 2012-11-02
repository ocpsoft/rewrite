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
package com.ocpsoft.pretty.faces.url;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

public class Segment
{
   private static final String SUFFIX = "#-p#";
   private static final String PREFIX = "#p-#";
   
   private static final Pattern TEMPLATE_PATTERN = Pattern.compile(PREFIX + "(\\d+)" + SUFFIX);

   private String template;
   private String regex;
   private final List<PathParameter> parameters = new ArrayList<PathParameter>();

   /**
    * Return a {@link String} representation of a parameter index value that can be re-discovered via
    * {@link #getTemplateMatcher(String)}
    */
   static public String parameterize(final int paramIndex)
   {
      return PREFIX + paramIndex + SUFFIX;
   }

   /**
    * Can be used to discover {@link String} representations of template parameters encoded via
    * {@link #parameterize(int)}
    */
   public static Matcher getTemplateMatcher(final String target)
   {
      return TEMPLATE_PATTERN.matcher(target);
   }

   /**
    * Get a {@link Matcher} populated with this {@link Segment}'s regular expression.
    */
   public Matcher getMatcher(final String target)
   {
      return Pattern.compile(regex).matcher(target);
   }

   /**
    * Get the parameterized (grouped) regular expression representing this {@link Segment}
    */
   public String getRegex()
   {
      return regex;
   }

   public void setRegex(final String regex)
   {
      this.regex = regex;
   }

   /**
    * Add a new parameter to this {@link Segment}'s internal list. These objects represent {@link PathParameter}s found
    * within this {@link Segment}.
    */
   public void addParameter(final PathParameter parameter)
   {
      this.parameters.add(parameter);
   }

   /**
    * Get the {@link PathParameter} at the given index.
    */
   public PathParameter getParameter(final int index)
   {
      return parameters.get(index);
   }

   /**
    * Get an ordered list of the current {@link PathParameter}s contained within this {@link Segment}.
    */
   public List<PathParameter> getParameters()
   {
      return Collections.unmodifiableList(parameters);
   }

   /**
    * Return the number of {@link PathParameter}s contained within this {@link Segment}.
    */
   public int numParameters()
   {
      return parameters.size();
   }

   /**
    * Get the template representing this {@link Segment}
    */
   public String getTemplate()
   {
      return template;
   }

   public void setTemplate(final String pattern)
   {
      this.template = pattern;
   }

   @Override
   public String toString()
   {
      return "Segment [template=" + template + ", regex=" + regex + "]";
   }

}
