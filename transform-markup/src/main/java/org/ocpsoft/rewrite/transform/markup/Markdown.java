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
package org.ocpsoft.rewrite.transform.markup;

import java.util.Arrays;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.transform.StringTransformer;

public class Markdown extends StringTransformer
{

   private String script;

   public Markdown()
   {
      this(true);
   }

   public Markdown(boolean completeDocument)
   {

      StringBuilder builder = new StringBuilder();
      builder.append("require 'maruku'\n");
      builder.append("doc = Maruku.new(input)\n");
      if (completeDocument) {
         builder.append("doc.to_html_document");
      }
      else {
         builder.append("doc.to_html");
      }
      this.script = builder.toString();

   }

   @Override
   public String transform(String input)
   {

      ScriptingContainer container = null;
      try {

         // prepare the scripting environment
         container = new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.TRANSIENT);
         container.setLoadPaths(Arrays.asList("ruby/maruku/lib"));

         // run the transformation and return the result
         container.put("input", input);
         Object result = container.runScriptlet(script);
         if (result != null) {
            return result.toString();
         }
         return null;

      }
      finally {
         if (container != null) {
            container.terminate();
         }
      }

   }

}
