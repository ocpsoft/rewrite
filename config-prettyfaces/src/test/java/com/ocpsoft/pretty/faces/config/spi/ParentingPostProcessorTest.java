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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

import jakarta.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.DigesterPrettyConfigParser;
import com.ocpsoft.pretty.faces.config.MockClassLoader;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigBuilder;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ParentingPostProcessorTest
{
   final ClassLoader mockResourceLoader = new MockClassLoader();

   private final Enumeration<String> initParameterNames = Collections.enumeration(new ArrayList<String>());

   @Test
   public void testParseParentIds() throws Exception
   {

      final PrettyConfigBuilder builder = new PrettyConfigBuilder();
      new DigesterPrettyConfigParser().parse(builder,
               getClass().getClassLoader().getResourceAsStream("parenting-pretty-config.xml"));
      PrettyConfig config = builder.build();

      assertThat(config.getMappingById("parent").getParentId()).isEqualTo("");
      assertThat(config.getMappingById("child").getParentId()).isEqualTo("parent");
      assertThat(config.getMappingById("grandchild").getParentId()).isEqualTo("child");
      assertThat(config.getMappingById("child2").getParentId()).isEqualTo("parent");
   }

   @Test
   public void testParentIdInheritsPatterns() throws SAXException, IOException
   {
      final ServletContext servletContext = Mockito.mock(ServletContext.class);

      Mockito.when(servletContext.getServletRegistrations()).thenReturn(new HashMap<>());
      Mockito.when(servletContext.getInitParameterNames()).thenReturn(initParameterNames);
      Mockito.when(servletContext.getInitParameter(PrettyContext.CONFIG_KEY)).thenReturn(null);
      Mockito.when(servletContext.getInitParameter(ClassLoaderConfigurationProvider.CLASSPATH_CONFIG_ENABLED))
               .thenReturn("false");
      Mockito.when(servletContext.getResourceAsStream(DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG))
               .thenReturn(mockPrettyConfigInputStream());

      final PrettyConfigurator configurator = new PrettyConfigurator(servletContext);
      configurator.configure();

      final PrettyConfig config = configurator.getConfig();

      assertThat(config.getMappings().size()).isEqualTo(4);
      assertThat(config.getMappingById("parent").getPattern()).isEqualTo("/parent");
      assertThat(config.getMappingById("child").getPattern()).isEqualTo("/parent/child/#{name}");
      assertThat(config.getMappingById("grandchild").getPattern()).isEqualTo("/parent/child/#{name}/grandchild/#{gname}");

      assertThat(config.getMappingById("grandchild").getPathValidators().size()).isEqualTo(2);
      assertThat(config.getMappingById("grandchild").getPathValidators().get(0).getValidatorIds()).isEqualTo("validator1");
      assertThat(config.getMappingById("grandchild").getPathValidators().get(1).getValidatorIds()).isEqualTo("validator2");
      assertThat(config.getMappingById("grandchild").getPathValidators().get(0).getIndex()).isEqualTo(0);
      assertThat(config.getMappingById("grandchild").getPathValidators().get(1).getIndex()).isEqualTo(1);

      // actions
      assertThat(config.getMappingById("grandchild").getActions().size()).isEqualTo(2);
      assertThat(config.getMappingById("grandchild").getActions().get(0).getAction().getELExpression()).isEqualTo("#{myBean.parentAction}");
      assertThat(config.getMappingById("grandchild").getActions().get(1).getAction().getELExpression()).isEqualTo("#{myBean.grandchildAction}");
      assertThat(config.getMappingById("child").getActions().size()).isEqualTo(2);
      assertThat(config.getMappingById("child").getActions().get(0).getAction().getELExpression()).isEqualTo("#{myBean.parentAction}");
      assertThat(config.getMappingById("child").getActions().get(1).getAction().getELExpression()).isEqualTo("#{myBean.childAction}");
      assertThat(config.getMappingById("child2").getActions().size()).isEqualTo(2);
      assertThat(config.getMappingById("child2").getActions().get(0).getAction().getELExpression()).isEqualTo("#{myBean.parentAction}");
      assertThat(config.getMappingById("child2").getActions().get(1).getAction().getELExpression()).isEqualTo("#{myBean.child2Action}");
      assertThat(config.getMappingById("parent").getActions().size()).isEqualTo(1);
      assertThat(config.getMappingById("parent").getActions().get(0).getAction().getELExpression()).isEqualTo("#{myBean.parentAction}");

      // query parameters
      assertThat(config.getMappingById("grandchild").getQueryParams().size()).isEqualTo(3);
      assertThat(config.getMappingById("grandchild").getQueryParams().get(0).getName()).isEqualTo("parent");
      assertThat(config.getMappingById("grandchild").getQueryParams().get(1).getName()).isEqualTo("child");
      assertThat(config.getMappingById("grandchild").getQueryParams().get(2).getName()).isEqualTo("grandchild");
      assertThat(config.getMappingById("child").getQueryParams().size()).isEqualTo(2);
      assertThat(config.getMappingById("child").getQueryParams().get(0).getName()).isEqualTo("parent");
      assertThat(config.getMappingById("child").getQueryParams().get(1).getName()).isEqualTo("child");
      assertThat(config.getMappingById("child2").getQueryParams().size()).isEqualTo(2);
      assertThat(config.getMappingById("child2").getQueryParams().get(0).getName()).isEqualTo("parent");
      assertThat(config.getMappingById("child2").getQueryParams().get(1).getName()).isEqualTo("child2");
      assertThat(config.getMappingById("parent").getQueryParams().size()).isEqualTo(1);
      assertThat(config.getMappingById("parent").getQueryParams().get(0).getName()).isEqualTo("parent");

   }

   private InputStream mockPrettyConfigInputStream()
   {
      return getClass().getClassLoader().getResourceAsStream("parenting-pretty-config.xml");
   }

}
