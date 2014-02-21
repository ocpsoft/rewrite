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

package com.ocpsoft.pretty.faces.config.spi;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationPostProcessor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParentingPostProcessor implements ConfigurationPostProcessor
{
   public static final String HIERARCHY_ENABLED_PARAM = "com.ocpsoft.pretty.INHERITABLE_CONFIG";
   private final List<UrlMapping> seen = new ArrayList<UrlMapping>();

   @Override
   public PrettyConfig processConfiguration(ServletContext context, PrettyConfig config)
   {
      String enabled = context.getInitParameter(HIERARCHY_ENABLED_PARAM);
      if ((enabled != null) && "false".equalsIgnoreCase(enabled.trim()))
      {
         return config;
      }

      List<UrlMapping> mappings = config.getMappings();
      for (UrlMapping m : mappings)
      {
         createAncestry(config, m);
      }
      return config;
   }

   private void createAncestry(PrettyConfig config, UrlMapping m)
   {
      if (m.hasParent() && !seen.contains(m))
      {
         UrlMapping parent = config.getMappingById(m.getParentId());
         if (parent == null)
         {
            throw new PrettyException("Error when building configuration for URL-mapping [" + m.getId() + ":"
                     + m.getPattern() + "] - the requested parentId [" + m.getParentId()
                     + "] does not exist in the configuration.");
         }
         if (parent.hasParent())
         {
            createAncestry(config, parent);
         }
         m.setPattern(parent.getPattern() + m.getPattern());
         mergeValidators(parent, m);
         mergeActions(parent, m);
         mergeQueryParams(parent, m);
         seen.add(m);
      }
   }

   private void mergeQueryParams(UrlMapping parent, UrlMapping child)
   {
      List<QueryParameter> result = new ArrayList<QueryParameter>();
      for (QueryParameter queryParam : parent.getQueryParams())
      {
         if (!result.contains(queryParam))
         {
            result.add(copy(queryParam));
         }
      }
      for (QueryParameter queryParam : child.getQueryParams())
      {
         if (!result.contains(queryParam))
         {
            result.add(copy(queryParam));
         }
      }
      child.setQueryParams(result);
   }

   private void mergeActions(UrlMapping parent, UrlMapping child)
   {
      List<UrlAction> result = new ArrayList<UrlAction>();
      for (UrlAction action : parent.getActions())
      {
         if (!result.contains(action) && action.isInheritable())
         {
            result.add(copy(action));
         }
      }
      for (UrlAction action : child.getActions())
      {
         if (!result.contains(action))
         {
            result.add(copy(action));
         }
      }
      child.setActions(result);
   }

   private void mergeValidators(UrlMapping parent, UrlMapping child)
   {
      List<PathValidator> result = new ArrayList<PathValidator>();
      List<PathValidator> validators = new ArrayList<PathValidator>();

      validators.addAll(parent.getPathValidators());
      validators.addAll(child.getPathValidators());

      int i = 0;
      for (PathValidator pv : validators)
      {
         PathValidator temp = copy(pv);
         temp.setIndex(i++);
         result.add(temp);
      }

      child.setPathValidators(result);
   }

   private QueryParameter copy(QueryParameter queryParameter)
   {
      QueryParameter result = new QueryParameter();
      result.setExpression(queryParameter.getExpression());
      result.setName(queryParameter.getName());
      result.setOnError(queryParameter.getOnError());
      result.setOnPostback(queryParameter.isOnPostback());
      result.setValidatorExpression(queryParameter.getValidatorExpression());
      result.setValidatorIds(queryParameter.getValidatorIds());
      return result;
   }

   private UrlAction copy(UrlAction urlAction)
   {
      UrlAction result = new UrlAction();
      result.setAction(urlAction.getAction());
      result.setOnPostback(urlAction.onPostback());
      result.setPhaseId(urlAction.getPhaseId());
      result.setInheritable(urlAction.isInheritable());
      return result;
   }

   private PathValidator copy(PathValidator pathValidator)
   {
      PathValidator result = new PathValidator();
      result.setIndex(pathValidator.getIndex());
      result.setOnError(pathValidator.getOnError());
      result.setValidatorIds(pathValidator.getValidatorIds());
      result.setValidatorExpression(pathValidator.getValidatorExpression());
      return result;
   }
}
