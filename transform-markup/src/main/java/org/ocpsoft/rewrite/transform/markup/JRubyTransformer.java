package org.ocpsoft.rewrite.transform.markup;

import java.util.List;

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
abstract class JRubyTransformer<T> extends StringTransformer
{

   private CompileMode compileMode = null;

   private CompatVersion compatVersion = null;

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

   ScriptingContainer container = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.TRANSIENT);

   public JRubyTransformer()
   {
      // the user may have set a custom CompileMode
      if (compileMode != null) {
         container.setCompileMode(compileMode);
      }

      // the user may have set a customn CompatVersion
      if (compatVersion != null) {
         container.setCompatVersion(compatVersion);
      }

      // scripts typically need to set the load path for 3rd party gems
      List<String> loadPaths = getLoadPaths();
      if (loadPaths != null && !loadPaths.isEmpty()) {
         container.setLoadPaths(loadPaths);
      }
   }

   @Override
   public final String transform(HttpServletRewrite event, String input)
   {
      try {

         Object result = null;
         synchronized (container) {
            // 'input' will be the string to transform
            container.put("input", input);

            // perform custom initialization of the container
            prepareContainer(container);

            // execute the script returned by the implementation
            result = runScript(container);
         }

         // the result must be a string
         return result != null ? result.toString() : null;

      }
      finally {
         if (container != null) {
            container.terminate();
         }
      }

   }

   /**
    * Can be used to customize the {@link ScriptingContainer} used for the transformation. The default implementation
    * does nothing.
    */
   protected void prepareContainer(ScriptingContainer container)
   {
      // nothing
   }

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
