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

public class Markdown extends JRubyTransformer
{

   private final static String SCRIPT = "require 'maruku'\n" +
            "doc = Maruku.new(input)\n" +
            "fullDocument ? doc.to_html_document : doc.to_html\n";

   private final boolean fullDocument;

   public Markdown()
   {
      this(true);
   }

   public Markdown(boolean fullDocument)
   {
      this.fullDocument = fullDocument;
   }

   @Override
   public List<String> getLoadPaths()
   {
      return Arrays.asList("ruby/maruku/lib");
   }

   @Override
   public Object runScript(ScriptingContainer container)
   {
      container.put("fullDocument", fullDocument);
      return container.runScriptlet(SCRIPT);
   }

}
