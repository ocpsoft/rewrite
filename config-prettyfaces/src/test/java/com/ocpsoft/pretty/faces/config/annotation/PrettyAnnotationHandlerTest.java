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
package com.ocpsoft.pretty.faces.config.annotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLBeanName;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;
import com.ocpsoft.pretty.faces.annotation.URLValidator;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigBuilder;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.el.ConstantExpression;
import com.ocpsoft.pretty.faces.el.LazyExpression;

import static org.assertj.core.api.Assertions.assertThat;

public class PrettyAnnotationHandlerTest
{

   @Test
   public void testNotAnnotatedClass()
   {
      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(NotAnnotatedClass.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(0);

   }

   @Test
   public void testSimpleMapping()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithSimpleMapping.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("simple");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(false);
      assertThat(mapping.isOnPostback()).isEqualTo(false);
      assertThat(mapping.getActions().size()).isEqualTo(0);
      assertThat(mapping.getQueryParams().size()).isEqualTo(0);
      assertThat(mapping.getPathValidators().size()).isEqualTo(0);

   }

   @Test
   public void testMappingWithPathValidation()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithMappingAndPathValidation.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("simple");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(true);
      assertThat(mapping.isOnPostback()).isEqualTo(true);
      assertThat(mapping.getActions().size()).isEqualTo(0);
      assertThat(mapping.getQueryParams().size()).isEqualTo(0);
      assertThat(mapping.getPathValidators().size()).isEqualTo(1);

      // check path validation
      PathValidator validator = mapping.getPathValidators().get(0);
      assertThat(validator.getIndex()).isEqualTo(0);
      assertThat(validator.getOnError()).isEqualTo("#{bean.action}");
      assertThat(validator.getValidatorIds()).isEqualTo("myValidator myOtherValidator");

   }

   @Test
   public void testMappingWithOneLazyExpressionAction()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithSingleLazyExpressionAction.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("singleLazyExpressionAction");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(true);
      assertThat(mapping.isOnPostback()).isEqualTo(true);
      assertThat(mapping.getQueryParams().size()).isEqualTo(0);
      assertThat(mapping.getActions().size()).isEqualTo(1);
      assertThat(mapping.getPathValidators().size()).isEqualTo(0);

      // validate action
      UrlAction action = mapping.getActions().get(0);
      assertThat(action.getPhaseId()).isEqualTo(PhaseId.RESTORE_VIEW);

      // validate PrettyExpression
      assertThat(action.getAction()).isNotNull();
      assertThat(action.getAction().getClass()).isEqualTo(LazyExpression.class);
      LazyExpression actionExpression = (LazyExpression) action.getAction();
      assertThat(actionExpression.getBeanClass()).isEqualTo(ClassWithSingleLazyExpressionAction.class);
      assertThat(actionExpression.getComponent()).isEqualTo("actionMethod");

   }

   @Test
   public void testMappingWithOneConstantExpressionAction()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithSingleConstantExpressionAction.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("singleConstantExpressionAction");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(true);
      assertThat(mapping.isOnPostback()).isEqualTo(true);
      assertThat(mapping.getQueryParams().size()).isEqualTo(0);
      assertThat(mapping.getActions().size()).isEqualTo(1);
      assertThat(mapping.getPathValidators().size()).isEqualTo(0);

      // validate action
      UrlAction action = mapping.getActions().get(0);
      assertThat(action.getPhaseId()).isEqualTo(PhaseId.RESTORE_VIEW);

      // validate PrettyExpression
      assertThat(action.getAction()).isNotNull();
      assertThat(action.getAction().getClass()).isEqualTo(ConstantExpression.class);
      assertThat(action.getAction().getELExpression()).isEqualTo("#{someBean.actionMethod}");

   }

   @Test
   public void testMappingWithMultipleActions()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithMultipleActions.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("multipleActions");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(true);
      assertThat(mapping.isOnPostback()).isEqualTo(true);
      assertThat(mapping.getQueryParams().size()).isEqualTo(0);
      assertThat(mapping.getActions().size()).isEqualTo(2);
      assertThat(mapping.getPathValidators().size()).isEqualTo(0);

      // validate first action
      UrlAction firstAction = mapping.getActions().get(0);
      assertThat(firstAction.getPhaseId()).isEqualTo(PhaseId.RENDER_RESPONSE);
      assertThat(firstAction.getAction()).isNotNull();
      assertThat(firstAction.getAction().getClass()).isEqualTo(LazyExpression.class);
      LazyExpression firstActionExpression = (LazyExpression) firstAction.getAction();
      assertThat(firstActionExpression.getBeanClass()).isEqualTo(ClassWithMultipleActions.class);
      assertThat(firstActionExpression.getComponent()).isEqualTo("actionMethod");

      // validate second action
      UrlAction secondAction = mapping.getActions().get(1);
      assertThat(secondAction.getPhaseId()).isEqualTo(PhaseId.INVOKE_APPLICATION);
      assertThat(secondAction.getAction()).isNotNull();
      assertThat(secondAction.getAction().getClass()).isEqualTo(LazyExpression.class);
      LazyExpression secondActionExpression = (LazyExpression) secondAction.getAction();
      assertThat(secondActionExpression.getBeanClass()).isEqualTo(ClassWithMultipleActions.class);
      assertThat(secondActionExpression.getComponent()).isEqualTo("actionMethod");

   }

   @Test
   public void testMappingWithSingleQueryParameter()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithSingleQueryParameter.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("singleQueryParamater");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(true);
      assertThat(mapping.isOnPostback()).isEqualTo(true);
      assertThat(mapping.getActions().size()).isEqualTo(0);
      assertThat(mapping.getQueryParams().size()).isEqualTo(1);
      assertThat(mapping.getPathValidators().size()).isEqualTo(0);

      // validate query parameter
      QueryParameter queryParameter = mapping.getQueryParams().get(0);
      assertThat(queryParameter.getName()).isEqualTo("myQueryParam");
      assertThat(queryParameter.isOnPostback()).isEqualTo(true);

      // validate PrettyExpression
      assertThat(queryParameter.getExpression()).isNotNull();
      assertThat(queryParameter.getExpression().getClass()).isEqualTo(ConstantExpression.class);
      assertThat(queryParameter.getExpression().getELExpression()).isEqualTo("#{myQueryParamBean.someParameter}");

   }

   @Test
   public void testMappingWithSingleQueryParameterAndValidation()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithSingleQueryParameterAndValidation.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(1);

      // validate mapping properties
      UrlMapping mapping = config.getMappings().get(0);
      assertThat(mapping.getId()).isEqualTo("singleQueryParamater");
      assertThat(mapping.getPattern()).isEqualTo("/some/url");
      assertThat(mapping.getViewId()).isEqualTo("/view.jsf");
      assertThat(mapping.isOutbound()).isEqualTo(true);
      assertThat(mapping.isOnPostback()).isEqualTo(true);
      assertThat(mapping.getActions().size()).isEqualTo(0);
      assertThat(mapping.getQueryParams().size()).isEqualTo(1);
      assertThat(mapping.getPathValidators().size()).isEqualTo(0);

      // validate query parameter
      QueryParameter queryParameter = mapping.getQueryParams().get(0);
      assertThat(queryParameter.getName()).isEqualTo("myQueryParam");
      assertThat(queryParameter.isOnPostback()).isEqualTo(false);
      assertThat(queryParameter.getOnError()).isEqualTo("#{bean.action}");
      assertThat(queryParameter.getValidatorIdList().size()).isEqualTo(2);
      assertThat(queryParameter.getValidatorIds()).isEqualTo("myValidator myOtherValidator");

      // validate PrettyExpression
      assertThat(queryParameter.getExpression()).isNotNull();
      assertThat(queryParameter.getExpression().getClass()).isEqualTo(ConstantExpression.class);
      assertThat(queryParameter.getExpression().getELExpression()).isEqualTo("#{myQueryParamBean.someParameter}");

   }

   @Test
   public void testClassWithMultipleMappings()
   {

      // create handler and process class
      PrettyAnnotationHandler handler = new PrettyAnnotationHandler(null);
      handler.visit(ClassWithMultipleMappings.class);

      // build configuration
      PrettyConfigBuilder configBuilder = new PrettyConfigBuilder();
      handler.build(configBuilder);
      PrettyConfig config = configBuilder.build();

      // no mappings added
      assertThat(config.getMappings()).isNotNull();
      assertThat(config.getMappings().size()).isEqualTo(2);

      // validate mapping properties for mappingA
      UrlMapping mappingA = config.getMappingById("mappingA");
      assertThat(mappingA).isNotNull();
      assertThat(mappingA.getId()).isEqualTo("mappingA");
      assertThat(mappingA.getPattern()).isEqualTo("/some/url/a");
      assertThat(mappingA.getViewId()).isEqualTo("/view.jsf");
      assertThat(mappingA.isOutbound()).isEqualTo(true);
      assertThat(mappingA.isOnPostback()).isEqualTo(true);
      assertThat(mappingA.getActions().size()).isEqualTo(1);
      assertThat(mappingA.getQueryParams().size()).isEqualTo(2);
      assertThat(mappingA.getPathValidators().size()).isEqualTo(0);
      assertThat(mappingA.getActions().get(0).getAction().getELExpression()).isEqualTo("#{multiMappingBean.actionForBoth}");

      // we don't know the order in which the query parameters are added
      List<String> queryParamExpressionsA = Arrays.asList(
               mappingA.getQueryParams().get(0).getExpression().getELExpression(),
               mappingA.getQueryParams().get(1).getExpression().getELExpression()
               );
      Collections.sort(queryParamExpressionsA);
      assertThat(queryParamExpressionsA.get(0)).isEqualTo("#{multiMappingBean.queryParameterForA}");
      assertThat(queryParamExpressionsA.get(1)).isEqualTo("#{multiMappingBean.queryParameterForBoth}");

      // validate mapping properties for mappingB
      UrlMapping mappingB = config.getMappingById("mappingB");
      assertThat(mappingB).isNotNull();
      assertThat(mappingB.getId()).isEqualTo("mappingB");
      assertThat(mappingB.getPattern()).isEqualTo("/some/url/b");
      assertThat(mappingB.getViewId()).isEqualTo("/view.jsf");
      assertThat(mappingB.isOutbound()).isEqualTo(true);
      assertThat(mappingB.isOnPostback()).isEqualTo(true);
      assertThat(mappingB.getActions().size()).isEqualTo(2);
      assertThat(mappingB.getQueryParams().size()).isEqualTo(1);
      assertThat(mappingB.getPathValidators().size()).isEqualTo(0);
      assertThat(mappingB.getQueryParams().get(0).getExpression().getELExpression()).isEqualTo("#{multiMappingBean.queryParameterForBoth}");

      // we don't know the order in which the actions are added
      List<String> actionExpressionsB = Arrays.asList(
               mappingB.getActions().get(0).getAction().getELExpression(),
               mappingB.getActions().get(1).getAction().getELExpression()
               );
      Collections.sort(actionExpressionsB);
      assertThat(actionExpressionsB.get(0)).isEqualTo("#{multiMappingBean.actionForBoth}");
      assertThat(actionExpressionsB.get(1)).isEqualTo("#{multiMappingBean.actionForB}");

   }

   /**
    * Simple class without any PrettyFaces annotations
    */
   public class NotAnnotatedClass
   {
      // nothing
   }

   /**
    * Simple class with a single {@link URLMapping} annotation
    */
   @URLMapping(id = "simple", pattern = "/some/url", viewId = "/view.jsf",
            outbound = false, onPostback = false)
   public class ClassWithSimpleMapping
   {
      // nothing
   }

   /**
    * Simple class with a single {@link URLMapping} annotation and a validation rule
    */
   @URLMapping(id = "simple", pattern = "/some/url", viewId = "/view.jsf",
            validation = @URLValidator(index = 0, onError = "#{bean.action}",
                     validatorIds = { "myValidator", "myOtherValidator" }))
   public class ClassWithMappingAndPathValidation
   {
      // nothing
   }

   /**
    * Simple class with a mapping and one annotated action method (lazy expression)
    */
   @URLMapping(id = "singleLazyExpressionAction", pattern = "/some/url", viewId = "/view.jsf")
   public class ClassWithSingleLazyExpressionAction
   {

      @URLAction
      public void actionMethod()
      {
         // nothing
      }

   }

   /**
    * Simple class with a mapping and one annotated action method (constant expression)
    */
   @URLMapping(id = "singleConstantExpressionAction", pattern = "/some/url", viewId = "/view.jsf")
   @URLBeanName("someBean")
   public class ClassWithSingleConstantExpressionAction
   {

      @URLAction
      public void actionMethod()
      {
         // nothing
      }

   }

   /**
    * Simple class with a mapping and an action method referenced by multiple {@link URLAction}s.
    */
   @URLMapping(id = "multipleActions", pattern = "/some/url", viewId = "/view.jsf")
   public class ClassWithMultipleActions
   {

      @URLActions(actions = {
               @URLAction(phaseId = URLAction.PhaseId.RENDER_RESPONSE),
               @URLAction(phaseId = URLAction.PhaseId.INVOKE_APPLICATION)
      })
      public void actionMethod()
      {
         // nothing
      }

   }

   /**
    * Simple class with a single mapping and a query parameter
    */
   @URLMapping(id = "singleQueryParamater", pattern = "/some/url", viewId = "/view.jsf")
   @URLBeanName("myQueryParamBean")
   public class ClassWithSingleQueryParameter
   {

      @URLQueryParameter("myQueryParam")
      private String someParameter;

   }

   /**
    * Simple class with a single mapping and a query parameter with validation
    */
   @URLMapping(id = "singleQueryParamater", pattern = "/some/url", viewId = "/view.jsf")
   @URLBeanName("myQueryParamBean")
   public class ClassWithSingleQueryParameterAndValidation
   {

      @URLQueryParameter(value = "myQueryParam", onPostback = false)
      @URLValidator(index = 0, onError = "#{bean.action}",
               validatorIds = { "myValidator", "myOtherValidator" })
      private String someParameter;

   }

   /*
    * Class with two mappings
    */
   @URLMappings(mappings = {
            @URLMapping(id = "mappingA", pattern = "/some/url/a", viewId = "/view.jsf"),
            @URLMapping(id = "mappingB", pattern = "/some/url/b", viewId = "/view.jsf")
   })
   @URLBeanName("multiMappingBean")
   public class ClassWithMultipleMappings
   {

      // assigned to both mappings
      @URLQueryParameter("q1")
      private String queryParameterForBoth;

      // assigned to both mappings
      @URLQueryParameter(value = "q2", mappingId = "mappingA")
      private String queryParameterForA;

      // assigned to both mappings
      @URLAction
      public void actionForBoth()
      {
         // nothing
      }

      // assigned to both mappings
      @URLAction(mappingId = "mappingB")
      public void actionForB()
      {
         // nothing
      }

   }
}
