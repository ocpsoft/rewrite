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
 * A {@link Transformer} that translates Textile markup into HTML.
 * 
 * @author Christian Kaltepoth
 */
public class Textile extends JRubyTransformer<Textile>
{

   private static final String SCRIPT = "require 'redcloth'\n" +
            "RedCloth.new(input).to_html\n";

   private final boolean fullDocument;

   private final HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder();

   /**
    * Create a {@link Transformer} instance that renders a full HTML document structure.
    */
   public static Textile fullDocument()
   {
      return new Textile(true);
   }

   /**
    * Create a {@link Transformer} instance that renders textile without adding HTML scaffold like a body or head.
    */
   public static Textile partialDocument()
   {
      return new Textile(false);
   }

   protected Textile(boolean fullDocument)
   {
      this.fullDocument = fullDocument;
   }

   /**
    * Sets the title of the rendered HTML document. Only applicable when rending a full document.
    */
   public Textile withTitle(String title)
   {
      this.documentBuilder.withTitle(title);
      return this;
   }

   /**
    * Adds a CSS stylesheet to the rendered HTML document. Only applicable when rending a full document.
    */
   public Textile addStylesheet(String url)
   {
      this.documentBuilder.addStylesheet(url);
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
            return documentBuilder.build(fragment.toString());
         }

         // output just the fragment
         else {
            return fragment.toString();
         }
      }
      return null;

   }

   @Override
   public Textile self()
   {
      return this;
   }

   @Override
   protected void prepareContainer(ScriptingContainer container)
   {}

   @Override
   protected Class<Textile> getTransformerType()
   {
      return Textile.class;
   }

}
