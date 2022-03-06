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
package com.ocpsoft.pretty.faces.config;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.RequestParameter;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.el.ConstantExpression;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingDigesterPrettyConfigParserTest
{
   private static final String CONFIG_PATH = "mock-pretty-config.xml";
   private PrettyConfig config;

   @Before
   public void configure() throws IOException, SAXException
   {
      final PrettyConfigBuilder builder = new PrettyConfigBuilder();
      new DigesterPrettyConfigParser().parse(builder, getClass().getClassLoader().getResourceAsStream(CONFIG_PATH));
      config = builder.build();
   }

   @Test
   public void testParse()
   {
      UrlMapping mapping = config.getMappingById("0");

      assertThat(mapping.getId()).isEqualTo("0");
      assertThat(mapping.getPattern()).isEqualTo("/project/#{viewProjectBean.projectId}/");
      assertThat(mapping.getViewId()).isEqualTo("#{viewProjectBean.getPrettyTarget}");

      List<UrlAction> actions = mapping.getActions();
      assertThat(actions).contains(new UrlAction("#{viewProjectBean.load}"));
      assertThat(actions).contains(new UrlAction("#{viewProjectBean.authorize}"));
   }

   @Test
   public void testOnPostbackDefaultsToTrue()
   {
      UrlMapping mapping = config.getMappingById("0");
      assertThat(mapping.isOnPostback()).isEqualTo(true);
   }

   @Test
   public void testOnPostbackSetToFalse()
   {
      UrlMapping mapping = config.getMappingById("1");
      assertThat(mapping.isOnPostback()).isEqualTo(false);
   }

   @Test
   public void testParseWithPostbackAction() throws Exception
   {
      UrlMapping mapping = config.getMappingById("1");
      assertThat(mapping.getActions().get(0).onPostback()).isFalse();
      assertThat(mapping.getActions().get(1).onPostback()).isTrue();
   }

   @Test
   public void testParseActionDefaultsToPostbackTrue() throws Exception
   {
      UrlMapping mapping = config.getMappingById("2");
      assertThat(mapping.getActions().get(0).onPostback()).isTrue();
      assertThat(mapping.getActions().get(1).onPostback()).isTrue();
   }

   @Test
   public void testParseWithAnyPhaseAction() throws Exception
   {
      UrlMapping mapping = config.getMappingById("2");
      assertThat(mapping.getActions().get(1).getPhaseId()).isEqualTo(PhaseId.ANY_PHASE);
   }

   @Test
   public void testParseWithPreRenderAction() throws Exception
   {
      UrlMapping mapping = config.getMappingById("3");
      assertThat(mapping.getActions().get(1).getPhaseId()).isEqualTo(PhaseId.RENDER_RESPONSE);
   }

   @Test
   public void testParseWithMappedQueryParam() throws Exception
   {
      UrlMapping mapping = config.getMappingById("4");
      List<QueryParameter> params = mapping.getQueryParams();

      assertThat(params.size()).isEqualTo(1);
      RequestParameter param = params.get(0);
      RequestParameter expected = new QueryParameter("user", null, new ConstantExpression("#{deleteUserBean.userName}"));
      assertThat(param).isEqualTo(expected);
   }

   @Test
   public void testParseWithMappedMultipleQueryParams() throws Exception
   {
      UrlMapping mapping = config.getMappingById("6");
      List<QueryParameter> params = mapping.getQueryParams();

      assertThat(params.size()).isEqualTo(2);
      RequestParameter name = new QueryParameter("name", null, new ConstantExpression("#{searchUserBean.userName}"));
      RequestParameter gender = new QueryParameter("gender", null, new ConstantExpression("#{searchUserBean.userGender}"));
      assertThat(params.toArray()).isEqualTo(new Object[]{name, gender});
   }

   @Test
   public void testParseWithNoQueryParams() throws Exception
   {
      UrlMapping mapping = config.getMappingById("7");
      List<QueryParameter> params = mapping.getQueryParams();
      assertThat(params.size()).isEqualTo(0);
   }

   @Test
   public void testParseWithPathValidators() throws Exception
   {
      UrlMapping mapping = config.getMappingById("validate");
      assertThat(mapping.getPattern()).isEqualTo("/validate/#{validationBean.pathInput}");
      assertThat(mapping.getPathValidators().size()).isEqualTo(2);
      assertThat(mapping.getPathValidators().get(0).getIndex()).isEqualTo(0);
      assertThat(mapping.getPathValidators().get(0).getValidatorIds()).isEqualTo("validator1");
      assertThat(mapping.getPathValidators().get(0).getOnError()).isEqualTo("#{validationBean.handle}");
      assertThat(mapping.getPathValidators().get(0).getValidatorExpression()).isEqualTo(null);

      assertThat(mapping.getPathValidators().get(1).getIndex()).isEqualTo(1);
      assertThat(mapping.getPathValidators().get(1).getValidatorIds()).isEqualTo("validator2");
      assertThat(mapping.getPathValidators().get(1).getOnError()).isEqualTo("#{validationBean.handle2}");
      assertThat(mapping.getPathValidators().get(1).getValidatorExpression().getELExpression()).isEqualTo("#{validationBean.validateMethod}");
      List<QueryParameter> params = mapping.getQueryParams();
      assertThat(params.size()).isEqualTo(1);
   }

   @Test
   public void testParseWithQueryValidators() throws Exception
   {
      UrlMapping mapping = config.getMappingById("validate");
      List<QueryParameter> params = mapping.getQueryParams();
      assertThat(params.size()).isEqualTo(1);

      QueryParameter p = params.get(0);
      assertThat(p.getValidatorIds()).isEqualTo("validator1 validator2");
      assertThat(p.getOnError()).isEqualTo("pretty:demo");
      assertThat(p.getName()).isEqualTo("p");
      assertThat(p.getValidatorExpression().getELExpression()).isEqualTo("#{validationBean.validateMethod}");

   }

   @Test
   public void testQueryParameterOnPostbackAttribute() throws Exception
   {
      UrlMapping mapping = config.getMappingById("8");
      List<QueryParameter> params = mapping.getQueryParams();
      assertThat(params.size()).isEqualTo(3);

      assertThat(params.get(0).getName()).isEqualTo("withoutAttribute");
      assertThat(params.get(0).isOnPostback()).isEqualTo(true);

      assertThat(params.get(1).getName()).isEqualTo("attributeSetToFalse");
      assertThat(params.get(1).isOnPostback()).isEqualTo(false);

      assertThat(params.get(2).getName()).isEqualTo("attributeSetToTrue");
      assertThat(params.get(2).isOnPostback()).isEqualTo(true);

   }

}
