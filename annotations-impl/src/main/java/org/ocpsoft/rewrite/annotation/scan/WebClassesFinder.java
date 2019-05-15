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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.annotation.ClassVisitorImpl;
import org.ocpsoft.rewrite.annotation.api.ClassVisitor;
import org.ocpsoft.rewrite.annotation.spi.ClassFinder;

/**
 * Implementation of {@link ClassFinder} that searches for classes in the <code>/WEB-INF/classes</code> directory of a
 * web application. Please note that this class is stateful. It should be used only for one call to
 * {@link #findClasses(ClassVisitorImpl)}.
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
   private final Set<String> processedClasses = new LinkedHashSet<String>();
   
	public static final String CLASS_EXTENSION = ".class";
	public static final int CLASS_EXTENSION_LENGTH = CLASS_EXTENSION.length();
	public static final String META_INF = "META-INF";

   /**
    * Initialization
    */
   public WebClassesFinder(ServletContext servletContext, ClassLoader classLoader, PackageFilter packageFilter,
            ByteCodeFilter byteCodeFilter)
   {
      super(servletContext, classLoader, packageFilter, byteCodeFilter);
   }
   
   @Override
   public void findClasses(ClassVisitor visitor)
   {
      try
      {
         // get the absolute URL of the classes folder
         URL classesFolderUrl = servletContext.getResource(CLASSES_FOLDER);
         if (classesFolderUrl != null) {
             // call recursive directory processing method
             processDirectory(classesFolderUrl, CLASSES_FOLDER, visitor);
             return;
         }
       	 classesFolderUrl = Thread.currentThread().getContextClassLoader().getResource("");
       	 if (classesFolderUrl != null) {
			processUrl(visitor, classesFolderUrl);
			return;
		}
         
         // abort if classes folder is missing
         log.warn("Cannot find classes folder");
      }
      catch (MalformedURLException e)
      {
         throw new IllegalStateException("Invalid URL: " + e.getMessage(), e);
      }
   }
   
   public void processUrl(ClassVisitor visitor, URL url) {
		try {
			if ("file".equals(url.getProtocol())) {
				File file = new File(url.getFile());
				scanDir(visitor, file, file.getAbsolutePath().length() + 1);
			} else if ("jar".equals(url.getProtocol())) {
				JarURLConnection connection = (JarURLConnection)url.openConnection();
				scanJar(visitor, connection);
			}
		} catch (Exception exception) {
			throw new IllegalArgumentException("Error scanning url '" + url.toExternalForm() + "'", exception);
		}
   }
	private void scanDir(ClassVisitor visitor, File file, int prefix) throws Exception {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				scanDir(visitor, child, prefix);
			}
		} else if (file.getName().endsWith(CLASS_EXTENSION)) {
			String className = getClassName(file.getAbsolutePath().substring(prefix));
			handleClassUrl(visitor, file.toURI().toURL(), className);
		}
	}
	private void scanJar(ClassVisitor visitor, JarURLConnection connection) throws Exception {
		for (Enumeration<JarEntry> enumeration = connection.getJarFile().entries(); enumeration.hasMoreElements();) {
			JarEntry entry = enumeration.nextElement();
			if (!entry.isDirectory() && !entry.getName().startsWith(META_INF) &&
					entry.getName().endsWith(CLASS_EXTENSION)) {
				String className = getClassName(entry.getName());
				URL url = new URL(connection.getURL(), entry.getName());
				handleClassUrl(visitor, url, className);
			}
		}
	}
	private void handleClassUrl(ClassVisitor visitor, URL url, String className) {
		if (mustProcessClass(className) && !processedClasses.contains(className)) {
			processedClasses.add(className);
			InputStream classFileStream = null;
			try {
				try {
					classFileStream = url.openStream();
				} catch (Exception e) {
					if (log.isDebugEnabled())
						log.debug("Cound not obtain InputStream for class: " + className, e);
				}
				processClass(className, classFileStream, visitor);
			} finally {
				try {
					if (classFileStream != null)
						classFileStream.close();
				} catch (IOException e) {
					if (log.isDebugEnabled())
						log.debug("Failed to close input stream: " + e.getMessage());
				}
			}
		}
	}	

/**
    * Scan for classes in a single directory. This method will call itself recursively if it finds other directories and
    * call {@link #processClass(String, InputStream, ClassVisitorImpl) when it finds a file ending with ".class" and
    * that is accepted by the {@link PackageFilter}
    * 
    * @param absoluteUrl The absolute URL of the WEB-INF node to scan
    * @param relativePath The path of the node inside the WEB-INF
    * @param visitor The visitor class to call for classes found
    * @throws MalformedURLException for invalid URLs
    */
   protected void processDirectory(URL absoluteUrl, String relativePath, ClassVisitor visitor)
            throws MalformedURLException
   {

      // log directory name on trace level
      if (log.isTraceEnabled())
      {
         log.trace("Processing directory: " + relativePath);
      }

      // Convert url to string, this will result in a full path of a node in an exploded war archive
      // let it be "file://opt/server/application/abc/expoded/ear/war/WEB-INF/classes/com/"
      String urlAsString = absoluteUrl.toString();

      Set<?> paths = servletContext.getResourcePaths(relativePath);
      if (paths == null || paths.isEmpty())
      {
         return;
      }

      // Process child nodes
      for (Object obj : paths)
      {
         // produces "/WEB-INF/classes/com/mycompany/"
         String childNodeName = obj.toString();

         // get last part of the node path (folder or class entry)
         // for example for childnode "/WEB-INF/classes/com/mycompany/" returns "mycompany/"
         String childNodeRelative = getChildNodeName(childNodeName);

         // get the folder of the node inside WEB-INF
         // for example for childnode "/WEB-INF/classes/com/mycompany/" returns "/WEB-INF/classes/com/"
         String webInfFolder = childNodeName.substring(0, childNodeName.length() - childNodeRelative.length());

         // Find relative base folder
         // produces "file://opt/server/application/abc/expoded/ear/war/"
         String urlBase = urlAsString.substring(0, urlAsString.length() - webInfFolder.length());

         // Create child node URL
         // produces "file://opt/server/application/abc/expoded/ear/war/WEB-INF/classes/com/mycompany/"
         URL childNodeUrl = new URL(urlBase + childNodeName);

         if (childNodeRelative.endsWith("/"))
         {
            // Recursive cal
            processDirectory(childNodeUrl, childNodeName, visitor);
         }
         if (childNodeRelative.endsWith(".class"))
         {
            handleClassEntry(childNodeUrl, childNodeName, visitor);
         }
      }
   }

   /**
    * Handles class entry in a WEB-INF.
    */
   private void handleClassEntry(URL entryUrl, String entryName, ClassVisitor visitor)
   {

      // build class name from relative name
      String className = getClassName(entryName.substring(CLASSES_FOLDER.length()));

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
             * Try to open the .class file. If an IOException is thrown, we will scan it anyway.
             */
            try
            {
              classFileStream = servletContext.getResourceAsStream(entryName);
            }
            catch (Exception e)
            {
               if (log.isDebugEnabled())
               {
                  log.debug("Could not obtain InputStream for class file: " + entryName, e);
               }
            }

            // analyze the class (with or without classFileStream)
            processClass(className, classFileStream, visitor);

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

   /**
    * @param path
    * @return last node in a a string representation of URL path. For example for "/a/b/c/d/" returns "d/", for
    *         "/a/b/c/d.class" returns "d.class"
    */
   private String getChildNodeName(String path)
   {
      String[] elements = path.split("/");
      int size = elements.length;
      String nodeName = elements[size - 1];
      return path.endsWith("/") ? nodeName + "/" : nodeName;
   }

   @Override
   public int priority()
   {
      return 0;
   }

}
