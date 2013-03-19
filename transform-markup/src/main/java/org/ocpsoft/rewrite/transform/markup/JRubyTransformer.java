package org.ocpsoft.rewrite.transform.markup;

import java.util.List;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.transform.StringTransformer;
import org.ocpsoft.rewrite.transform.Transformer;

/**
 * Base class for {@link Transformer} implementations that use JRuby scripts.
 * 
 * @author Christian Kaltepoth
 */
public abstract class JRubyTransformer extends StringTransformer
{

   /**
    * Return the load paths to use for {@link ScriptingContainer#setLoadPaths(List)}.
    */
   public abstract List<String> getLoadPaths();

   /**
    * This method must perform the transformation using the supplied {@link ScriptingContainer}. The container is pre
    * initialized with a variable <code>input</code> which should be used by the script to perform the transformation.
    */
   public abstract Object runScript(ScriptingContainer container);

   @Override
   public final String transform(String input)
   {

      ScriptingContainer container = null;
      try {

         // as this container is only used locally, a single threaded transient one works fine
         container = new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.TRANSIENT);

         // scripts typically need to set the load path for 3rd party gems
         List<String> loadPaths = getLoadPaths();
         if (loadPaths != null && !loadPaths.isEmpty()) {
            container.setLoadPaths(loadPaths);
         }

         // 'input' will be the string to transform
         container.put("input", input);

         // perform custom initialization of the container
         prepareContainer(container);

         // execute the script returned by the implementation
         Object result = runScript(container);

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

}
