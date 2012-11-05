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
package org.ocpsoft.rewrite.annotation.scan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.annotation.api.ClassVisitor;
import org.ocpsoft.rewrite.annotation.spi.ClassFinder;

/**
 * Implementation of {@link ClassFinder} that searches for classes in the JAR archives found in the
 * <code>/WEB-INF/lib/</code> directory of a web application.
 *
 * @author Christian Kaltepoth
 */
public class WebLibFinder extends AbstractClassFinder
{

   /**
    * The web application directory containing the JAR files
    */
   private final static String LIB_FOLDER = "/WEB-INF/lib/";

   /**
    * Initialization
    */
   public WebLibFinder(ServletContext servletContext, ClassLoader classLoader, PackageFilter packageFilter,
            ByteCodeFilter byteCodeFilter)
   {
      super(servletContext, classLoader, packageFilter, byteCodeFilter);
   }

   @Override
   public void findClasses(ClassVisitor visitor)
   {

      // catch MalformedURLException
      try
      {

         // get URL of the lib folder
         URL libFolderUrl = servletContext.getResource(LIB_FOLDER);

         // abort on missing lib folder
         if (libFolderUrl == null)
         {
            log.warn("Cannot find " + LIB_FOLDER + " folder!");
            return;
         }

         // call getResourcePaths to get directory entries
         Set<?> paths = servletContext.getResourcePaths(LIB_FOLDER);

         // loop over all entries of the directory
         for (Object relativePath : paths)
         {

            // get full URL of the current directory entry
            URL entryUrl = servletContext.getResource(relativePath.toString());

            // we are only interested in JAR files
            if (entryUrl.getPath().endsWith(".jar"))
            {
               processJarFile(entryUrl, visitor);
            }

         }

      }
      catch (MalformedURLException e)
      {
         throw new IllegalStateException("Invalid URL: " + e.getMessage(), e);
      }
   }

   /**
    * Process a single JAR file in the <code>/WEB-INF/lib/</code> directory.
    *
    * @param jarUrl The URL of the JAR file
    * @param visitor The visitor to call on classes
    */
   private void processJarFile(URL jarUrl, ClassVisitor visitor)
   {

      // log file name on debug lvel
      if (log.isDebugEnabled())
      {
         log.debug("Processing JAR file: " + jarUrl.toString());
      }

      // Use a JarInputStream to read the archive
      JarInputStream jarStream = null;

      // catch any type of IOException
      try
      {

         // open the JAR stream
         jarStream = new JarInputStream(jarUrl.openStream());

         // Loop over all entries of the archive
         JarEntry jarEntry = null;
         while ((jarEntry = jarStream.getNextJarEntry()) != null)
         {

            // We are only interested in java class files
            if (jarEntry.getName().endsWith(".class"))
            {

               // generate FQCN from entry
               String className = getClassName(jarEntry.getName());

               // check name against PackageFilter
               if (mustProcessClass(className))
               {

                  // analyze this class
                  processClass(className, jarStream, visitor);

               }

            }

         }

      }
      catch (IOException e)
      {
         log.error("Failed to read JAR file: " + jarUrl.toString(), e);
      }
      finally
      {
         // Close the stream if it has been opened
         if (jarStream != null)
         {
            try
            {
               jarStream.close();
            }
            catch (IOException e)
            {
               // ignore IO failures on close
            }
         }
      }

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
