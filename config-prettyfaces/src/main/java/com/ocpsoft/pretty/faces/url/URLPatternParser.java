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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.el.ExpressionProcessorRunner;
import com.ocpsoft.pretty.faces.el.Expressions;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class URLPatternParser
{

   private static final Pattern EL_REGEX_PATTERN = Pattern.compile(Expressions.EL_REGEX);

   private final String originalPattern;
   private boolean elPattern;
   private URL urlPattern = null;
   private Pattern urlElPattern;
   private List<Segment> pathSegments = new ArrayList<Segment>();
   private List<PathParameter> pathParameters = new ArrayList<PathParameter>();

   /**
    * Set the pattern for which this parser will match. Find and replace all el expressions with regular expressions to
    * extract values from parsed URLs. Also extract all parameter names from expressions, and replace with valid EL
    * 
    * @param pattern Pattern to use as a parse template
    */
   public URLPatternParser(final String pattern)
   {
      originalPattern = pattern;
      Matcher expressionMatcher = EL_REGEX_PATTERN.matcher(pattern);
      StringBuffer segmentableExpressions = new StringBuffer();

      elPattern = false;
      if (pattern.indexOf('|') != -1) {
         elPattern = true;
      }

      /*
       * Extract path parameters
       */
      int paramIndex = 0;
      while (expressionMatcher.find())
      {
         elPattern = true;
         String expression = expressionMatcher.group(1);
         PathParameter param = ExpressionProcessorRunner.process(expression);
         param.setPosition(paramIndex);
         pathParameters.add(param);

         expressionMatcher.appendReplacement(segmentableExpressions, Segment.parameterize(paramIndex));
         paramIndex++;
      }
      expressionMatcher.appendTail(segmentableExpressions);
      this.pathParameters = Collections.unmodifiableList(pathParameters);

      URL segmentedPattern = new URL(segmentableExpressions.toString());

      /*
       * Extract path segments, overlaying regexes found during parameter
       * discovery.
       */
      for (String segmentPattern : segmentedPattern.getSegments())
      {
         Segment segment = new Segment();
         segment.setTemplate(segmentPattern);
         StringBuffer regex = new StringBuffer();

         Matcher parameterMatcher = Segment.getTemplateMatcher(segmentPattern);
         while (parameterMatcher.find())
         {
            String group = parameterMatcher.group(1);
            PathParameter parameter = pathParameters.get(Integer.valueOf(group));
            segment.addParameter(parameter);
            parameterMatcher.appendReplacement(regex, "(" + parameter.getRegex() + ")");
         }
         parameterMatcher.appendTail(regex);

         segment.setRegex(regex.toString());
         pathSegments.add(segment);
      }

      List<String> regexSegments = new ArrayList<String>();
      for (Segment s : pathSegments)
      {
         regexSegments.add(s.getRegex());
      }

      urlPattern = new URL(regexSegments, segmentedPattern.getMetadata().copy());
      if (elPattern) {
         urlElPattern = Pattern.compile(urlPattern.toURL());
      }
      this.pathSegments = Collections.unmodifiableList(pathSegments);
   }

   /**
    * Return true if this parser matches the given URL, otherwise, return false.
    */
   public boolean matches(final URL target)
   {
      if (elPattern) {
         return urlElPattern.matcher(target.toURL()).matches();
      }
      else {
         return urlPattern.toURL().equals(target.toURL());
      }
   }

   /**
    * Return true if this parser matches the given URL, otherwise, return false.
    */
   @Deprecated
   public boolean matches(final String target)
   {
      URL url = new URL(target);
      return matches(url);
   }

   /**
    * Return the list of parameters specified by this pattern.
    */
   public List<PathParameter> getPathParameters()
   {
      return pathParameters;
   }

   /**
    * Builds a list of PathParameters for this UrlPattern, extracted from the provided URL (assuming a match is found).
    * This list excludes duplicate named parameters.
    */
   public List<PathParameter> parse(final URL url)
   {
      List<PathParameter> result = new ArrayList<PathParameter>();

      String inboundUrl = url.toURL();
      if (this.matches(url))
      {
         Iterator<Segment> iter = pathSegments.iterator();
         while (iter.hasNext())
         {
            Segment segment = iter.next();

            String regex = "";
            if (url.hasLeadingSlash() && !"/".equals(url.toURL()))
            {
               regex += "/";
            }

            regex += segment.getRegex();
            if (iter.hasNext() || url.hasTrailingSlash())
            {
               regex += "/";
            }

            Matcher segmentMatcher = Pattern.compile(regex).matcher(inboundUrl);
            if (segmentMatcher.find())
            {
               for (int j = 0; j < segment.numParameters(); j++)
               {
                  String value = segmentMatcher.group(j + 1);
                  PathParameter param = segment.getParameter(j).copy();
                  param.setValue(value);
                  result.add(param);
               }

               int regionEnd = segmentMatcher.end();

               inboundUrl = inboundUrl.substring(regionEnd - 1);
            }
            else
            {
               throw new PrettyException("Error parsing url: <" + url
                        + ">, a parameter did not match compiled segment in pattern: " + originalPattern);
            }
         }
      }
      else
      {
         throw new IllegalArgumentException("The given URL: " + url + ", cannot be parsed by the pattern: "
                  + originalPattern);
      }
      return result;
   }

   /**
    * URL encoding/decoding is not a concern of this method.
    * 
    * @param params Array of Object parameters, in order, to be substituted for mapping pattern values or el
    *           expressions. This method will call the toString() method on each object provided.
    *           <p>
    *           If only one param is specified and it is an instance of List, the list items will be used as parameters
    *           instead. An empty list or a single null parameter are both treated as if no parameters were specified.
    *           </p>
    *           E.g: getMappedUrl(12,55,"foo","bar") for a pattern of /#{el.one}/#{el.two}/#{el.three}/#{el.four}/ will
    *           return the String: "/12/55/foo/bar/"
    * @return A URL based on this object's urlPatten, with values substituted for el expressions in the order provided
    */
   public URL getMappedURL(final Object... params)
   {
      URL result = null;
      if (params != null)
      {
         Object[] parameters = params;

         /*
          * Check to see if our parameters were provided as a single List
          */
         if ((params.length == 1) && (params[0] != null) && (params[0] instanceof List<?>))
         {
            List<?> list = (List<?>) params[0];
            if (list.size() == 0)
            {
               parameters = new Object[0];
            }
            else
            {
               parameters = list.toArray(params);
            }
         }
         else if ((params.length == 1) && (params[0] == null))
         {
            parameters = new Object[0];
         }

         /*
          * Assert that we have the proper number of parameters.
          */
         if (getParameterCount() != parameters.length)
         {
            throw new PrettyException("Invalid number of path parameters supplied for pattern: " + originalPattern
                     + ", expected <" + getParameterCount() + ">, but got <" + parameters.length + ">");
         }

         /*
          * Build the result URL
          */
         int paramIndex = 0;
         List<String> resultSegments = new ArrayList<String>();
         for (Segment segment : pathSegments)
         {
            String template = segment.getTemplate();
            Matcher parameterMatcher = Segment.getTemplateMatcher(template);

            StringBuffer sb = new StringBuffer();
            while (parameterMatcher.find())
            {
               /*
                * We need to escape $ and \ because they have a special meaning when 
                * used in Matcher#appendReplacement(). From the docs:
                * 
                * Note that backslashes (\) and dollar signs ($) in the replacement string 
                * may cause the results to be different than if it were being treated as a 
                * literal replacement string. Dollar signs may be treated as references to 
                * captured subsequences as described above, and backslashes are used to 
                * escape literal characters in the replacement string.
                */
               String replacement = parameters[paramIndex].toString()
                        .replace("$", "\\$")
                        .replace("\\", "\\\\");

               parameterMatcher.appendReplacement(sb, replacement);
               paramIndex++;
            }
            parameterMatcher.appendTail(sb);
            resultSegments.add(sb.toString());
         }
         result = new URL(resultSegments, urlPattern.getMetadata());
      }
      else if (getParameterCount() > 0)
      {
         throw new PrettyException("Invalid number of parameters supplied: " + originalPattern + ", expected <"
                  + getParameterCount() + ">, got <0>");
      }

      return result;
   }

   /**
    * Get the number of URL parameters that this parser expects to find in any given input string
    * 
    * @return Number of parameters
    */
   public int getParameterCount()
   {
      return pathParameters.size();
   }

   public Object getPattern()
   {
      return originalPattern;
   }

   /**
    * @return whether the URL pattern is an expression language
    */
   public boolean isElPattern()
   {
      return elPattern;
   }

}