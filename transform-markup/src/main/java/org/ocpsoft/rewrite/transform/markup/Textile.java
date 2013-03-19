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

public class Textile extends JRubyTransformer
{

   private static final String SCRIPT = "require 'redcloth'\n" +
            "RedCloth.new(input).to_html\n";

   private boolean fullDocument = true;

   private Textile()
   {
      // hide constructor
   }

   public static Textile transformer()
   {
      return new Textile();
   }

   public Textile fullDocument()
   {
      this.fullDocument = true;
      return this;
   }

   public Textile partialDocument()
   {
      this.fullDocument = false;
      return this;
   }

   @Override
   public List<String> getLoadPaths()
   {
      return Arrays.asList("ruby/redcloth/lib");
   }

   @Override
   public Object runScript(ScriptingContainer container)
   {

      Object fragment = container.runScriptlet(SCRIPT);

      if (fragment != null) {

         // create complete HTML structure
         if (fullDocument) {
            StringBuilder result = new StringBuilder();
            result.append("<!DOCTYPE html>\n");
            result.append("<html>\n<body>\n");
            result.append(fragment.toString());
            result.append("</body>\n</html>\n");
            return result.toString();
         }

         // output just the fragment
         else {
            return fragment.toString();
         }
      }
      return null;

   }

}
