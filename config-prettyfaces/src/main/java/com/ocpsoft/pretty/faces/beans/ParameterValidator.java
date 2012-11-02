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
package com.ocpsoft.pretty.faces.beans;

import java.io.IOException;
import java.util.List;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.config.mapping.PathParameter;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.url.URL;
import com.ocpsoft.pretty.faces.util.FacesElUtils;
import com.ocpsoft.pretty.faces.util.NullComponent;

/**
 * @author Lincoln Baxter, III <lincoln@ocpsoft.com>
 */
public class ParameterValidator
{
   private static final Log log = LogFactory.getLog(ParameterInjector.class);
   private static final FacesElUtils elUtils = new FacesElUtils();

   public void validateParameters(final FacesContext context)
   {
      log.trace("Validating parameters.");
      PrettyContext prettyContext = PrettyContext.getCurrentInstance(context);
      URL url = prettyContext.getRequestURL();
      UrlMapping mapping = prettyContext.getCurrentMapping();

      if (mapping != null)
      {
         validatePathParams(context, url, mapping);
         validateQueryParams(context, mapping);
      }
   }

   private void validatePathParams(final FacesContext context, final URL url, final UrlMapping mapping)
   {
      List<PathParameter> params = mapping.getPatternParser().parse(url);

      PathParameter currentParameter = new PathParameter();
      PathValidator currentPathValidator = new PathValidator();
      String currentValidatorId = "";
      try
      {
         for (PathParameter param : params)
         {
            currentParameter = param;

            List<PathValidator> validators = mapping.getValidatorsForPathParam(param);

            if (validators != null && validators.size() > 0)
            {
               String value = param.getValue();
               Object coerced = elUtils.coerceToType(context, param.getExpression().getELExpression(), value);
               for (PathValidator pv : validators)
               {
                  currentPathValidator = pv;
                  for (String id : pv.getValidatorIdList())
                  {
                     currentValidatorId = id;
                     Validator validator = context.getApplication().createValidator(id);
                     validator.validate(context, new NullComponent(), coerced);
                  }
                  if (pv.getValidatorExpression() != null)
                  {
                     elUtils.invokeMethod(context, pv.getValidatorExpression().getELExpression(),
                              new Class<?>[] { FacesContext.class, UIComponent.class, Object.class },
                              new Object[] { context, new NullComponent(), coerced });
                  }
               }
            }
         }
      }
      catch (ELException e)
      {
         FacesMessage message = new FacesMessage("Could not coerce value [" + currentParameter.getValue()
                  + "] on mappingId [" + mapping.getId() + "] to type in location [" + currentParameter.getExpression()
                  + "]");
         handleValidationFailure(context, message, currentPathValidator.getOnError());
      }
      catch (ValidatorException e)
      {
         handleValidationFailure(context, e.getFacesMessage(), currentPathValidator.getOnError());
      }
      catch (FacesException e)
      {
         FacesMessage message = new FacesMessage("Error occurred invoking validator with id [" + currentValidatorId
                  + "] on mappingId [" + mapping.getId() + "] parameter [" + currentParameter.getExpression()
                  + "] at position [" + currentParameter.getPosition() + "]");
         handleValidationFailure(context, message, currentPathValidator.getOnError());
      }
   }

   private void validateQueryParams(final FacesContext context, final UrlMapping mapping)
   {
      QueryParameter currentParameter = new QueryParameter();
      String currentValidatorId = "";
      try
      {
         List<QueryParameter> params = mapping.getQueryParams();
         for (QueryParameter param : params)
         {
            if (param.hasValidators() || (param.getValidatorExpression() != null))
            {
               currentParameter = param;

               String name = param.getName();
               String el = param.getExpression().getELExpression();

               if (elUtils.getExpectedType(context, el).isArray())
               {
                  String[] values = context.getExternalContext().getRequestParameterValuesMap().get(name);
                  if (values != null)
                  {
                     Object coerced = elUtils.coerceToType(context, el, values);
                     for (String id : param.getValidatorIdList())
                     {
                        currentValidatorId = id;
                        Validator validator = context.getApplication().createValidator(id);
                        validator.validate(context, new NullComponent(), coerced);
                     }
                     if (param.getValidatorExpression() != null)
                     {
                        elUtils.invokeMethod(context, param.getValidatorExpression().getELExpression(),
                                 new Class<?>[] { FacesContext.class, UIComponent.class, Object.class },
                                 new Object[] { context, new NullComponent(), coerced });
                     }
                  }
               }
               else
               {
                  String value = context.getExternalContext().getRequestParameterMap().get(name);
                  if (value != null)
                  {
                     Object coerced = elUtils.coerceToType(context, el, value);
                     for (String id : param.getValidatorIdList())
                     {
                        currentValidatorId = id;
                        Validator validator = context.getApplication().createValidator(id);
                        validator.validate(context, new NullComponent(), coerced);
                     }
                     if (param.getValidatorExpression() != null)
                     {
                        elUtils.invokeMethod(context, param.getValidatorExpression().getELExpression(),
                                 new Class<?>[] { FacesContext.class, UIComponent.class, Object.class },
                                 new Object[] { context, new NullComponent(), coerced });
                     }
                  }
               }
            }
         }
      }
      catch (ELException e)
      {
         FacesMessage message = new FacesMessage("Could not coerce value [" + currentParameter.getValue()
                  + "] on mappingId [" + mapping.getId() + "] to type ["
                  + elUtils.getExpectedType(context, currentParameter.getExpression().getELExpression()) + "]");
         handleValidationFailure(context, message, currentParameter.getOnError());
      }
      catch (ValidatorException e)
      {
         handleValidationFailure(context, e.getFacesMessage(), currentParameter.getOnError());
      }
      catch (FacesException e)
      {
         FacesMessage message = new FacesMessage("Error occurred invoking validator with id [" + currentValidatorId
                  + "] on mappingId [" + mapping.getId() + "] parameter [" + currentParameter.getName() + "]");
         handleValidationFailure(context, message, currentParameter.getOnError());
      }
   }

   private void handleValidationFailure(final FacesContext context, final FacesMessage message, String onError)
   {
      boolean continueToFaces = false;
      if ((onError != null) && !"".equals(onError.trim()))
      {
         if (elUtils.isEl(onError))
         {
            Object result = elUtils.invokeMethod(context, onError);
            if (result == null)
            {
               continueToFaces = true;
            }
            else
            {
               onError = result.toString();
            }
         }

         if (onError != null)
         {
            String viewId = context.getViewRoot().getViewId();
            context.getApplication().getNavigationHandler().handleNavigation(context, viewId, onError);
         }
      }

      if (!context.getResponseComplete() && !continueToFaces)
      {
         HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
         try
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            context.responseComplete();
         }
         catch (IOException e1)
         {
            throw new PrettyException(e1);
         }
      }
   }

}
