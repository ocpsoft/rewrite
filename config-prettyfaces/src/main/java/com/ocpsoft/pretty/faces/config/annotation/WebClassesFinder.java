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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

/**
 * Implementation of {@link ClassFinder} that searches for classes in the <code>/WEB-INF/classes</code> directory of a web
 * application. Please note that this class is stateful. It should be used only for one class to
 * {@link #findClasses(PrettyAnnotationHandler)}.
 * 
 * @author Christian Kaltepoth
 */
public class WebClassesFinder extends AbstractClassFinder
{

   /**
    * The name of the <code>classes</code> directory
    */
   private final static String CLASSES_FOLDER = "/WEB-INF/classes/";

   /**
    * Manage a set of classes already processed
    */
   private final Set<String> processedClasses = new HashSet<String>();
   
   /**
    * Initialization
    */
   public WebClassesFinder(ServletContext servletContext, ClassLoader classLoader, PackageFilter packageFilter)
   {
      super(servletContext, classLoader, packageFilter);
   }

   /*
    * @see
    * com.ocpsoft.pretty.faces.config.annotation.ClassFinder#findClasses(com
    * .ocpsoft.pretty.config.annotation.PrettyAnnotationHandler)
    */
   public void findClasses(PrettyAnnotationHandler handler)
   {
      try
      {
         // we start the recursive scan in the classes folder
         URL classesFolderUrl = servletContext.getResource(CLASSES_FOLDER);

         // abort if classes folder is missing
         if (classesFolderUrl == null)
         {
            log.warn("Cannot find classes folder: " + CLASSES_FOLDER);
            return;
         }

         // call recursive directory processing method
         processDirectory(classesFolderUrl, classesFolderUrl, handler);

      }
      catch (MalformedURLException e)
      {
         throw new IllegalStateException("Invalid URL: " + e.getMessage(), e);
      }
   }

   /**
    * Scan for classes in a single directory. This method will call itself
    * recursively if it finds other directories and call
    * {@link #processClassURL(URL, PrettyAnnotationHandler)} when it finds a
    * file ending with ".class" and that is accepted by the
    * {@link PackageFilter}
    * 
    * @param directoryUrl
    *           The URL of the directory to scan
    * @param handler
    *           The handler class for classes found
    * @throws MalformedURLException
    *            for invalid URLs
    */
   protected void processDirectory(URL classesFolderUrl, URL directoryUrl, PrettyAnnotationHandler handler) throws MalformedURLException
   {
      
      // only the path of the classes folder URL is required in this method
      String classesFolderPath = classesFolderUrl.getPath();
      
      // log directory name on trace level
      if (log.isTraceEnabled())
      {
         log.trace("Processing directory: " + directoryUrl.toString());
      }

      // get the directory name relative to the '/WEB-INF/classes/' folder
      String relativeDirectoryName = getPathRelativeToClassesFolder(directoryUrl.getPath(), classesFolderPath);
      
      // call getResourcePaths to get directory entries
      Set<?> paths = servletContext.getResourcePaths(CLASSES_FOLDER+relativeDirectoryName);

      if (paths != null) {

         // loop over all entries of the directory
         for (Object relativePath : paths)
         {

            // get full URL for this entry
            URL entryUrl = servletContext.getResource(relativePath.toString());

            // if this URL ends with .class it is a Java class
            if (entryUrl.getPath().endsWith(".class"))
            {

               // the name of the entry relative to the '/WEB-INF/classes/' folder
               String entryRelativeName = getPathRelativeToClassesFolder(entryUrl.getPath(), classesFolderPath);

               // build class name from relative name
               String className = getClassName(entryRelativeName);

               // check filter
               if (mustProcessClass(className) && !processedClasses.contains(className))
               {

                  // mark this class as processed
                  processedClasses.add(className);

                  // the class file stream
                  InputStream classFileStream = null;

                  // close the stream in finally block
                  try
                  {

                     /*
                      * Try to open the .class file. If an IOException is thrown,
                      * we will scan it anyway.
                      */
                     try
                     {
                        classFileStream = entryUrl.openStream();
                     }
                     catch (IOException e)
                     {
                        if (log.isDebugEnabled())
                        {
                           log.debug("Cound not obtain InputStream for class file: " + entryUrl.toString(), e);
                        }
                     }

                     // analyze the class (with or without classFileStream)
                     processClass(className, classFileStream, handler);

                  }
                  finally
                  {
                     try
                     {
                        if (classFileStream != null)
                        {
                           classFileStream.close();
                        }
                     }
                     catch (IOException e)
                     {
                        if (log.isDebugEnabled())
                        {
                           log.debug("Failed to close input stream: " + e.getMessage());
                        }
                     }
                  }
               }

            }

            // if this URL ends with a slash, its a directory
            if (entryUrl.getPath().endsWith("/"))
            {

               // walk down the directory
               processDirectory(classesFolderUrl, entryUrl, handler);

            }
         }
      }
   }

   /**
    * This method will create a path relative to the '/WEB-INF/classes/' folder
    * for the given path. It will first try to find the '/WEB-INF/classes/'
    * suffix in the path to do so. If this suffix cannot be found (can happen
    * when using the jetty-maven-plugin with 'jetty:run' goal), the method will
    * try to build the relative name by stripping the path of the
    * '/WEB-INF/classes/' folder which must be supplied to the method. The
    * method will throw an {@link IllegalArgumentException} if the relative path
    * could not be build.
    * 
    * @param path
    *           The path to build the relative path for
    * @param classesFolderPath
    *           the known path of the '/WEB-INF/classes/' folder.
    * @return the relative name of the path
    */
   private String getPathRelativeToClassesFolder(String path, String classesFolderPath)
   {

      // first try to find the '/WEB-INF/classes' suffix
      String result = stripKnownPrefix(path, CLASSES_FOLDER);

      // alternative: try to strip the full path of the '/WEB-INF/classes/' folder 
      if (result == null)
      {
         result = stripKnownPrefix(path, classesFolderPath);
      }

      // none of the two methods worked?
      if (result == null)
      {
         throw new IllegalArgumentException("Unable to build path relative to '/WEB-INF/classes/' from: "+path);
      }

      return result;

   }

}
