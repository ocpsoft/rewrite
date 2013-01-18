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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.DigesterPrettyConfigParser;
import com.ocpsoft.pretty.faces.config.MockClassLoader;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigBuilder;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;

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

      assertEquals("", config.getMappingById("parent").getParentId());
      assertEquals("parent", config.getMappingById("child").getParentId());
      assertEquals("child", config.getMappingById("grandchild").getParentId());
      assertEquals("parent", config.getMappingById("child2").getParentId());
   }

   @Test
   public void testParentIdInheritsPatterns() throws SAXException, IOException
   {
      final ServletContext servletContext = EasyMock.createNiceMock(ServletContext.class);

      EasyMock.expect(servletContext.getMajorVersion()).andReturn(3).anyTimes();
      EasyMock.expect(servletContext.getInitParameterNames()).andReturn(initParameterNames).anyTimes();
      EasyMock.expect(servletContext.getInitParameter(PrettyContext.CONFIG_KEY)).andReturn(null).anyTimes();
      EasyMock.expect(servletContext.getInitParameter(ClassLoaderConfigurationProvider.CLASSPATH_CONFIG_ENABLED))
               .andReturn("false").anyTimes();
      EasyMock.expect(servletContext.getResourceAsStream(DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG))
               .andReturn(mockPrettyConfigInputStream()).anyTimes();

      final PrettyConfigurator configurator = new PrettyConfigurator(servletContext);
      EasyMock.replay(servletContext);
      configurator.configure();

      final PrettyConfig config = configurator.getConfig();

      assertEquals(4, config.getMappings().size());
      assertEquals("/parent", config.getMappingById("parent").getPattern());
      assertEquals("/parent/child/#{name}", config.getMappingById("child").getPattern());
      assertEquals("/parent/child/#{name}/grandchild/#{gname}", config.getMappingById("grandchild").getPattern());

      assertEquals(2, config.getMappingById("grandchild").getPathValidators().size());
      assertEquals("validator1", config.getMappingById("grandchild").getPathValidators().get(0).getValidatorIds());
      assertEquals("validator2", config.getMappingById("grandchild").getPathValidators().get(1).getValidatorIds());
      assertEquals(0, config.getMappingById("grandchild").getPathValidators().get(0).getIndex());
      assertEquals(1, config.getMappingById("grandchild").getPathValidators().get(1).getIndex());

      // actions
      assertEquals(2, config.getMappingById("grandchild").getActions().size());
      assertEquals("#{myBean.parentAction}", config.getMappingById("grandchild").getActions().get(0).getAction().getELExpression());
      assertEquals("#{myBean.grandchildAction}", config.getMappingById("grandchild").getActions().get(1).getAction().getELExpression());
      assertEquals(2, config.getMappingById("child").getActions().size());
      assertEquals("#{myBean.parentAction}", config.getMappingById("child").getActions().get(0).getAction().getELExpression());
      assertEquals("#{myBean.childAction}", config.getMappingById("child").getActions().get(1).getAction().getELExpression());
      assertEquals(2, config.getMappingById("child2").getActions().size());
      assertEquals("#{myBean.parentAction}", config.getMappingById("child2").getActions().get(0).getAction().getELExpression());
      assertEquals("#{myBean.child2Action}", config.getMappingById("child2").getActions().get(1).getAction().getELExpression());
      assertEquals(1, config.getMappingById("parent").getActions().size());
      assertEquals("#{myBean.parentAction}", config.getMappingById("parent").getActions().get(0).getAction().getELExpression());

      // query parameters
      assertEquals(3, config.getMappingById("grandchild").getQueryParams().size());
      assertEquals("parent", config.getMappingById("grandchild").getQueryParams().get(0).getName());
      assertEquals("child", config.getMappingById("grandchild").getQueryParams().get(1).getName());
      assertEquals("grandchild", config.getMappingById("grandchild").getQueryParams().get(2).getName());
      assertEquals(2, config.getMappingById("child").getQueryParams().size());
      assertEquals("parent", config.getMappingById("child").getQueryParams().get(0).getName());
      assertEquals("child", config.getMappingById("child").getQueryParams().get(1).getName());
      assertEquals(2, config.getMappingById("child2").getQueryParams().size());
      assertEquals("parent", config.getMappingById("child2").getQueryParams().get(0).getName());
      assertEquals("child2", config.getMappingById("child2").getQueryParams().get(1).getName());
      assertEquals(1, config.getMappingById("parent").getQueryParams().size());
      assertEquals("parent", config.getMappingById("parent").getQueryParams().get(0).getName());

   }

   private InputStream mockPrettyConfigInputStream()
   {
      return getClass().getClassLoader().getResourceAsStream("parenting-pretty-config.xml");
   }

}
