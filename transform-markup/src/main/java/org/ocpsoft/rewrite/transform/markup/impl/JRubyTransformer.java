package org.ocpsoft.rewrite.transform.markup.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.jruby.CompatVersion;
import org.jruby.RubyInstanceConfig.CompileMode;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.StringTransformer;
import org.ocpsoft.rewrite.transform.Transformer;

/**
 * Base class for {@link Transformer} implementations that use JRuby scripts.
 * 
 * @author Christian Kaltepoth
 */
public abstract class JRubyTransformer<T extends JRubyTransformer<T>> extends StringTransformer
{
   static final String CONTAINER_STORE_KEY = JRubyTransformer.class.getName() + "_CONTAINER_INSTANCE";

   private CompileMode compileMode = CompileMode.JIT;

   private CompatVersion compatVersion = CompatVersion.RUBY2_0;

   /**
    * Return the load paths to use for {@link ScriptingContainer#setLoadPaths(List)}.
    */
   public abstract List<String> getLoadPaths();

   /**
    * This method must perform the transformation using the supplied {@link ScriptingContainer}. The container is pre
    * initialized with a variable <code>input</code> which should be used by the script to perform the transformation.
    */
   public abstract Object runScript(ScriptingContainer container);

   /**
    * Just returns the current object as the correct type for make the fluent builder work with subclasses.
    */
   public abstract T self();

   public JRubyTransformer()
   {}

   @Override
   public final String transform(HttpServletRewrite event, String input)
   {
      ScriptingContainer container = getContainer(event.getServletContext());
      try {

         Object result = null;
         // 'input' will be the string to transform
         container.put("input", input);

         // execute the script returned by the implementation
         result = runScript(container);

         // the result must be a string
         return result != null ? result.toString() : null;
      }
      finally {
         if (container != null)
            container.clear();
      }

   }

   private ScriptingContainer getContainer(ServletContext context)
   {
      @SuppressWarnings("unchecked")
      Map<Class<T>, ScriptingContainer> storage = (Map<Class<T>, ScriptingContainer>) context
               .getAttribute(CONTAINER_STORE_KEY);
      if (storage == null)
      {
         storage = new ConcurrentHashMap<Class<T>, ScriptingContainer>();
         context.setAttribute(CONTAINER_STORE_KEY, storage);
      }

      ScriptingContainer cachedContainer = storage.get(getTransformerType());
      if (cachedContainer == null)
      {
         cachedContainer = new ScriptingContainer(LocalContextScope.CONCURRENT, LocalVariableBehavior.TRANSIENT);
         cachedContainer.setRunRubyInProcess(false);

         storage.put(getTransformerType(), cachedContainer);

         // the user may have set a custom CompileMode
         if (compileMode != null) {
            cachedContainer.setCompileMode(compileMode);
         }

         // the user may have set a customn CompatVersion
         if (compatVersion != null) {
            cachedContainer.setCompatVersion(compatVersion);
         }

         // scripts typically need to set the load path for 3rd party gems
         List<String> loadPaths = getLoadPaths();
         if (loadPaths != null && !loadPaths.isEmpty()) {
            cachedContainer.getLoadPaths().addAll(loadPaths);
         }

         // perform custom initialization of the container
         prepareContainer(cachedContainer);
      }

      return cachedContainer;
   }

   abstract protected Class<T> getTransformerType();

   abstract protected void prepareContainer(ScriptingContainer container);

   /**
    * Allows to customize the {@link CompileMode} used by the JRuby runtime.
    */
   public T compileMode(CompileMode compileMode)
   {
      this.compileMode = compileMode;
      return self();
   }

   /**
    * Allows to customize the {@link CompatVersion} used by the JRuby runtime.
    */
   public T compatVersion(CompatVersion compatVersion)
   {
      this.compatVersion = compatVersion;
      return self();
   }

}
