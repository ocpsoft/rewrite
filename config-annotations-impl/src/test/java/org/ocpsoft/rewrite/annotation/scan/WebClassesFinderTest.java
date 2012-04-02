/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.annotation.scan;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.annotation.api.ClassVisitor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class WebClassesFinderTest
{

   @Test
   public void testWebClassesFinder() throws Exception
   {

      // URLs of simulated directories and files
      URL classesUrl = new URL("file:/somewhere/WEB-INF/classes/");
      URL packageUrl = new URL("file:/somewhere/WEB-INF/classes/package/");
      URL classUrl = new URL("file:/somewhere/WEB-INF/classes/package/TestClass.class");

      // Contents of directories
      Set<String> classesDirectory = new HashSet<String>(Arrays.asList("/WEB-INF/classes/package/"));
      Set<String> packageDirectory = new HashSet<String>(Arrays.asList("/WEB-INF/classes/package/TestClass.class"));

      // Create filter accepting all packages
      PackageFilter filter = new PackageFilter(null);

      // Mock of ServletContext that knows the directories and files
      ServletContext servletContext = Mockito.mock(ServletContext.class);
      Mockito.when(servletContext.getResource("/WEB-INF/classes/")).thenReturn(classesUrl);
      Mockito.when(servletContext.getResourcePaths("/WEB-INF/classes/")).thenReturn(classesDirectory);
      Mockito.when(servletContext.getResource("/WEB-INF/classes/package/")).thenReturn(packageUrl);
      Mockito.when(servletContext.getResourcePaths("/WEB-INF/classes/package/")).thenReturn(packageDirectory);
      Mockito.when(servletContext.getResource("/WEB-INF/classes/package/TestClass.class")).thenReturn(classUrl);

      // ClassLoader that knows the test class
      ClassLoader classLoader = Mockito.mock(ClassLoader.class);
      Mockito.when(classLoader.loadClass("package.TestClass")).thenReturn((Class) ClassFinderTestBean.class);

      // Create the ByteCodeFilter
      Set<Class<? extends Annotation>> types = new HashSet<Class<? extends Annotation>>();
      types.add(TestAnnotation.class);
      ByteCodeFilter byteCodeFilter = new ByteCodeFilter(types);

      // Mock the visitor for verification
      ClassVisitor classVisitor = Mockito.mock(ClassVisitor.class);

      // Run finder
      WebClassesFinder finder = new WebClassesFinder(servletContext, classLoader, filter, byteCodeFilter);
      finder.findClasses(classVisitor);

      // Verify that the PrettyAnnotationHandler has been notified
      Mockito.verify(classVisitor).visit(ClassFinderTestBean.class);

   }

   @Test
   public void testWebClassesFinderWithJettyRunGoal() throws Exception
   {

      // URLs do not contain '/WEB-INF/classes/' but the Maven '/target/classes/' folder
      URL classesUrl = new URL("file:/somewhere/myproject/target/classes/");
      URL packageUrl = new URL("file:/somewhere/myproject/target/classes/package/");
      URL classUrl = new URL("file:/somewhere/myproject/target/classes/package/TestClass.class");

      // Contents of directories
      Set<String> classesDirectory = new HashSet<String>(Arrays.asList("/WEB-INF/classes/package/"));
      Set<String> packageDirectory = new HashSet<String>(Arrays.asList("/WEB-INF/classes/package/TestClass.class"));

      // Create filter accepting all packages
      PackageFilter filter = new PackageFilter(null);

      // Mock of ServletContext that knows the directories and files
      ServletContext servletContext = Mockito.mock(ServletContext.class);
      Mockito.when(servletContext.getResource("/WEB-INF/classes/")).thenReturn(classesUrl);
      Mockito.when(servletContext.getResourcePaths("/WEB-INF/classes/")).thenReturn(classesDirectory);
      Mockito.when(servletContext.getResource("/WEB-INF/classes/package/")).thenReturn(packageUrl);
      Mockito.when(servletContext.getResourcePaths("/WEB-INF/classes/package/")).thenReturn(packageDirectory);
      Mockito.when(servletContext.getResource("/WEB-INF/classes/package/TestClass.class")).thenReturn(classUrl);

      // ClassLoader that knows the test class
      ClassLoader classLoader = Mockito.mock(ClassLoader.class);
      Mockito.when(classLoader.loadClass("package.TestClass")).thenReturn((Class) ClassFinderTestBean.class);

      // We will need the ByteCodeFilter
      Set<Class<? extends Annotation>> types = new HashSet<Class<? extends Annotation>>();
      types.add(TestAnnotation.class);
      ByteCodeFilter byteCodeFilter = new ByteCodeFilter(types);

      // Mock the visitor for verification
      ClassVisitor classVisitor = Mockito.mock(ClassVisitor.class);

      // Run finder
      WebClassesFinder finder = new WebClassesFinder(servletContext, classLoader, filter, byteCodeFilter);
      finder.findClasses(classVisitor);

      // Verify that the PrettyAnnotationHandler has been notified
      Mockito.verify(classVisitor).visit(ClassFinderTestBean.class);

   }

}
