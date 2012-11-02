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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigBuilder;
import com.ocpsoft.pretty.faces.config.annotation.ClassFinder;
import com.ocpsoft.pretty.faces.config.annotation.PackageFilter;
import com.ocpsoft.pretty.faces.config.annotation.PrettyAnnotationHandler;
import com.ocpsoft.pretty.faces.config.annotation.WebClassesFinder;
import com.ocpsoft.pretty.faces.config.annotation.WebLibFinder;
import com.ocpsoft.pretty.faces.el.LazyBeanNameFinder;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AnnotationConfigurationProvider implements ConfigurationProvider
{
   private static final Log log = LogFactory.getLog(AnnotationConfigurationProvider.class);

   public static final String CONFIG_SCAN_LIB_DIR = "com.ocpsoft.pretty.SCAN_LIB_DIRECTORY";
   public static final String CONFIG_BASE_PACKAGES = "com.ocpsoft.pretty.BASE_PACKAGES";

   public PrettyConfig loadConfiguration(ServletContext servletContext)
   {
      String packageFilters = servletContext.getInitParameter(CONFIG_BASE_PACKAGES);

      if ((packageFilters != null) && packageFilters.trim().equalsIgnoreCase("none"))
      {
         log.debug("Annotation scanning has is disabled!");
         return null;
      }

      PackageFilter packageFilter = new PackageFilter(packageFilters);
      LazyBeanNameFinder beanNameFinder = new LazyBeanNameFinder(servletContext);
      PrettyAnnotationHandler annotationHandler = new PrettyAnnotationHandler(beanNameFinder);

      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      if (classloader == null)
      {
         classloader = this.getClass().getClassLoader();
      }

      List<ClassFinder> classFinders = new ArrayList<ClassFinder>();

      // we will always scan /WEB-INF/classes
      classFinders.add(new WebClassesFinder(servletContext, classloader, packageFilter));

      // does the user want to scan /WEB-INF/lib ?
      String jarConfig = servletContext.getInitParameter(CONFIG_SCAN_LIB_DIR);
      if ((jarConfig != null) && jarConfig.trim().equalsIgnoreCase("true"))
      {
         classFinders.add(new WebLibFinder(servletContext, classloader, packageFilter));
      }

      for (ClassFinder finder : classFinders)
      {
         finder.findClasses(annotationHandler);
      }

      PrettyConfigBuilder builder = new PrettyConfigBuilder();
      annotationHandler.build(builder);
      return builder.build();
   }
}
