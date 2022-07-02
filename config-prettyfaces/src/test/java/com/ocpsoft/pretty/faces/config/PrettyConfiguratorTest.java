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
import java.util.HashMap;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.spi.ClassLoaderConfigurationProvider;
import com.ocpsoft.pretty.faces.config.spi.DefaultXMLConfigurationProvider;

import static org.assertj.core.api.Assertions.assertThat;

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
      final ServletContext servletContext = Mockito.mock(ServletContext.class);

      Mockito.when(servletContext.getServletRegistrations()).thenReturn(new HashMap<>());
      Mockito.when(servletContext.getInitParameter(PrettyContext.CONFIG_KEY)).thenReturn(null);
      Mockito.when(servletContext.getResourceAsStream(DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG)).thenReturn(null);

      final PrettyConfigurator configurator = new PrettyConfigurator(servletContext);

      configurator.configure();
      final PrettyConfig config = configurator.getConfig();
      assertThat(config.getMappings().size()).isEqualTo(10);
   }

   @Test
   public void testInitParameterLocationConfiguration() throws SAXException, IOException
   {
      final ServletContext servletContext = Mockito.mock(ServletContext.class);

      Mockito.when(servletContext.getServletRegistrations()).thenReturn(new HashMap<>());
      Mockito.when(servletContext.getInitParameter(PrettyContext.CONFIG_KEY)).thenReturn("car.xml, cdr.xml");
      Mockito.when(servletContext.getInitParameter(ClassLoaderConfigurationProvider.CLASSPATH_CONFIG_ENABLED)).thenReturn("false");
      Mockito.when(servletContext.getResourceAsStream("car.xml")).thenReturn(mockPrettyConfigInputStream());
      Mockito.when(servletContext.getResourceAsStream("cdr.xml")).thenReturn(mockPrettyConfigInputStream());
      Mockito.when(servletContext.getResourceAsStream(DefaultXMLConfigurationProvider.DEFAULT_PRETTY_FACES_CONFIG)).thenReturn(mockPrettyConfigInputStream());

      final PrettyConfigurator configurator = new PrettyConfigurator(servletContext);
      configurator.configure();
      final PrettyConfig config = configurator.getConfig();
      assertThat(config.getMappings().size()).isEqualTo(30);

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
