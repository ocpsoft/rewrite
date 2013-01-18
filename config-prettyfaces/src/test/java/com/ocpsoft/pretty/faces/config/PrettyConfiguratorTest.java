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
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.spi.ClassLoaderConfigurationProvider;
import com.ocpsoft.pretty.faces.config.spi.DefaultXMLConfigurationProvider;

/**
 * @author Aleksei Valikov
 */
public class PrettyConfiguratorTest
{
   final ClassLoader mockResourceLoader = new MockClassLoader(mockPrettyConfigURL());
   final ClassLoader mockEmptyResourceLoader = new MockClassLoader();

   @Test
   public void testMETAINFConfiguration() throws SAXException, IOException
   {
      final ServletContext servletContext = EasyMock.createNiceMock(ServletContext.class);

      EasyMock.expect(servletContext.getMajorVersion()).andReturn(3).anyTimes();
      EasyMock.expect(servletContext.getInitParameter(PrettyContext.CONFIG_KEY)).andReturn(null).anyTimes();
      EasyMock.expect(servletContext.getResourceAsStream(DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG)).andReturn(null).anyTimes();

      final PrettyConfigurator configurator = new PrettyConfigurator(servletContext);

      EasyMock.replay(servletContext);
      configurator.configure();
      final PrettyConfig config = configurator.getConfig();
      Assert.assertEquals(10, config.getMappings().size());
   }

   @Test
   public void testInitParameterLocationConfiguration() throws SAXException, IOException
   {
      final ServletContext servletContext = EasyMock.createNiceMock(ServletContext.class);

      EasyMock.expect(servletContext.getMajorVersion()).andReturn(3).anyTimes();
      EasyMock.expect(servletContext.getInitParameter(PrettyContext.CONFIG_KEY)).andReturn("car.xml, cdr.xml").anyTimes();
      EasyMock.expect(servletContext.getInitParameter(ClassLoaderConfigurationProvider.CLASSPATH_CONFIG_ENABLED)).andReturn("false").anyTimes();
      EasyMock.expect(servletContext.getResourceAsStream("car.xml")).andReturn(mockPrettyConfigInputStream()).anyTimes();
      EasyMock.expect(servletContext.getResourceAsStream("cdr.xml")).andReturn(mockPrettyConfigInputStream()).anyTimes();
      EasyMock.expect(servletContext.getResourceAsStream(DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG)).andReturn(mockPrettyConfigInputStream()).anyTimes();

      final PrettyConfigurator configurator = new PrettyConfigurator(servletContext);
      EasyMock.replay(servletContext);
      configurator.configure();
      final PrettyConfig config = configurator.getConfig();
      Assert.assertEquals(30, config.getMappings().size());

   }

   private InputStream mockPrettyConfigInputStream()
   {
      return getClass().getClassLoader().getResourceAsStream("mock-pretty-config.xml");
   }

   private URL mockPrettyConfigURL()
   {
      return getClass().getClassLoader().getResource("mock-pretty-config.xml");
   }

}
