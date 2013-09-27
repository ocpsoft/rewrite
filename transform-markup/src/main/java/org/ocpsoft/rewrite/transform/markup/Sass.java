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
import java.util.List;

import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.transform.Transformer;
import org.ocpsoft.rewrite.transform.markup.impl.JRubyTransformer;

/**
 * A {@link Transformer} that translates SASS files into CSS.
 * 
 * @author Christian Kaltepoth
 */
public class Sass extends JRubyTransformer<Sass>
{

   private static final String SCRIPT = "require 'sass'\n" +
            "engine = Sass::Engine.new(input, :syntax => :scss, :cache => false)\n" +
            "engine.render\n";

   /**
    * Create a {@link Transformer} that compiles SASS files into CSS.
    */
   public static Sass compiler()
   {
      return new Sass();
   }

   protected Sass()
   {}

   @Override
   public List<String> getLoadPaths()
   {
      return Arrays.asList("ruby/sass/lib");
   }

   @Override
   public Object runScript(ScriptingContainer container)
   {
      return container.runScriptlet(SCRIPT);
   }

   @Override
   public Sass self()
   {
      return this;
   }

   @Override
   protected void prepareContainer(ScriptingContainer container)
   {}

   @Override
   protected Class<Sass> getTransformerType()
   {
      return Sass.class;
   }
}
