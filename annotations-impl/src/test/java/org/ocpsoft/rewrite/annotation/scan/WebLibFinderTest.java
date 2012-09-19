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

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.annotation.api.ClassVisitor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class WebLibFinderTest
{

   @Test
   public void testWebLibFinder() throws Exception
   {

      // URLs of simulated directories and files
      URL libUrl = new URL("file:/somewhere/WEB-INF/lib/");
      URL jarUrl = new URL("file", null, 0, "/somewhere/WEB-INF/lib/mylib.jar", new TestURLStreamHandler());

      // Contents of the lib directory
      Set<String> libDirectory = new HashSet<String>(Arrays.asList("/WEB-INF/lib/mylib.jar"));

      // Create filter accepting all packages
      PackageFilter filter = new PackageFilter(null);

      // Mock of ServletContext that knows our directories and files
      ServletContext servletContext = Mockito.mock(ServletContext.class);
      Mockito.when(servletContext.getResource("/WEB-INF/lib/")).thenReturn(libUrl);
      Mockito.when(servletContext.getResourcePaths("/WEB-INF/lib/")).thenReturn(libDirectory);
      Mockito.when(servletContext.getResource("/WEB-INF/lib/mylib.jar")).thenReturn(jarUrl);

      // ClassLoader that knows the test class
      ClassLoader classLoader = Mockito.mock(ClassLoader.class);
      Mockito.when(classLoader.loadClass(ClassFinderTestBean.class.getName()))
               .thenReturn((Class) ClassFinderTestBean.class);

      // We will need the ByteCodeFilter
      Set<Class<? extends Annotation>> types = new HashSet<Class<? extends Annotation>>();
      types.add(TestAnnotation.class);
      ByteCodeFilter byteCodeFilter = new ByteCodeFilter(types);

      // Mock the visitor for verification
      ClassVisitor classVisitor = Mockito.mock(ClassVisitor.class);

      // Run finder
      WebLibFinder finder = new WebLibFinder(servletContext, classLoader, filter, byteCodeFilter);
      finder.findClasses(classVisitor);

      // Verify that the PrettyAnnotationHandler has been notified
      Mockito.verify(classVisitor).visit(ClassFinderTestBean.class);

   }

   /**
    * A custom {@link URLStreamHandler} that will always return an in-memory JAR archive containing only the
    * {@link ClassFinderTestBean} class.
    */
   private final class TestURLStreamHandler extends URLStreamHandler
   {
      protected URLConnection openConnection(URL u) throws IOException
      {
         return new URLConnection(u)
         {
            @Override
            public void connect() throws IOException
            {
               // nothing to do
            }

            @Override
            public InputStream getInputStream() throws IOException
            {
               try
               {
                  // location of the class on the classpath and in the archive
                  final String classLocation = ClassFinderTestBean.class.getName().replace('.', '/') + ".class";

                  // read the original class file from the classpath
                  ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                  InputStream classStream = classLoader.getResourceAsStream(classLocation);
                  assertNotNull("Cannot find test class", classStream);

                  // prepare the in-memory JAR file
                  ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
                  JarOutputStream jarOutput = new JarOutputStream(byteArrayStream);

                  // write the class file to the archive
                  jarOutput.putNextEntry(new ZipEntry(classLocation));
                  byte[] buffer = new byte[255];
                  for (int len; (len = classStream.read(buffer)) != -1;)
                  {
                     jarOutput.write(buffer, 0, len);
                  }
                  jarOutput.closeEntry();

                  // close the JAR archive and return the InputStream
                  jarOutput.close();
                  return new ByteArrayInputStream(byteArrayStream.toByteArray());

               }
               catch (IOException e)
               {
                  throw new IllegalStateException(e);
               }
            }
         };
      }
   }

}
