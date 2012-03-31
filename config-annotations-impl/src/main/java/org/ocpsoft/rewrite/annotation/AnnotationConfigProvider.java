package org.ocpsoft.rewrite.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.annotation.scan.ByteCodeFilter;
import org.ocpsoft.rewrite.annotation.scan.PackageFilter;
import org.ocpsoft.rewrite.annotation.scan.WebClassesFinder;
import org.ocpsoft.rewrite.annotation.scan.WebLibFinder;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.annotation.spi.ClassFinder;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

public class AnnotationConfigProvider extends HttpConfigurationProvider
{
   private final Logger log = Logger.getLogger(AnnotationConfigProvider.class);

   public static final String CONFIG_SCAN_LIB_DIR = "com.ocpsoft.pretty.SCAN_LIB_DIRECTORY";
   public static final String CONFIG_BASE_PACKAGES = "com.ocpsoft.pretty.BASE_PACKAGES";

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Configuration getConfiguration(ServletContext servletContext)
   {

      /*
       * ======================================================
       * ============[ Configuration parameters ]==============
       * ======================================================
       */

      // retrieve the optional package filter configuration parameter
      String packageFilters = servletContext.getInitParameter(CONFIG_BASE_PACKAGES);

      // does the user want to scan the webapp lib directory
      boolean scanLibDir = false;
      String jarConfig = servletContext.getInitParameter(CONFIG_SCAN_LIB_DIR);
      if ((jarConfig != null) && jarConfig.trim().equalsIgnoreCase("true")) {
         scanLibDir = true;
      }

      // users can disable annotation scanning
      if ((packageFilters != null) && packageFilters.trim().equalsIgnoreCase("none")) {
         log.debug("Annotation scanning has is disabled!");
         return null;
      }

      /*
       * ======================================================
       * ============[ Prepare scanning process ]==============
       * ======================================================
       */

      // the byte code filter needs to know the annotations to look for
      Set<Class<? extends Annotation>> annotationType = new HashSet<Class<? extends Annotation>>();

      // list of annotation handlers for the ClassVisitor
      List<AnnotationHandler<Annotation>> annotationHandlers = new ArrayList<AnnotationHandler<Annotation>>();

      // load the implementations of the AnnotationHandler SPI
      @SuppressWarnings("unchecked")
      Iterator<AnnotationHandler<Annotation>> handlerIterator = ServiceLoader.load(AnnotationHandler.class).iterator();
      while (handlerIterator.hasNext()) {
         AnnotationHandler<Annotation> handler = handlerIterator.next();
         annotationHandlers.add(handler);
         annotationType.add(handler.handles());
      }

      // this class will identify the classes that should be scanned without loading them
      ByteCodeFilter byteCodeFilter = new ByteCodeFilter(annotationType);

      // users can configure the annotation scanner to scan only specific packages
      PackageFilter packageFilter = new PackageFilter(packageFilters);

      // ClassVisitor will process all classes that ByteCodeFilter considers as worth scanning them
      ClassVisitorImpl classVisitor = new ClassVisitorImpl(annotationHandlers);

      // fallback to some other classloder if there is no context class loader
      ClassLoader classloader = Thread.currentThread().getContextClassLoader();
      if (classloader == null) {
         classloader = this.getClass().getClassLoader();
      }

      /*
       * ======================================================
       * =============[ Scanning process starts ]==============
       * ======================================================
       */

      // compile a list of class finders to run
      List<ClassFinder> classFinders = new ArrayList<ClassFinder>();
      classFinders.add(new WebClassesFinder(servletContext, classloader, packageFilter, byteCodeFilter));
      if (scanLibDir) {
         classFinders.add(new WebLibFinder(servletContext, classloader, packageFilter, byteCodeFilter));
      }

      // start the scanning process
      for (ClassFinder finder : classFinders) {
         finder.findClasses(classVisitor);
      }

      // return the rules collected by the class visitor
      return classVisitor;
   }

}
