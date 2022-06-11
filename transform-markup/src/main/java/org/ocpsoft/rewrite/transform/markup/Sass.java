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

import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.StringTransformer;
import org.ocpsoft.rewrite.transform.Transformer;
import sass.embedded_protocol.EmbeddedSass;

/**
 * A {@link Transformer} that translates SASS files into CSS.
 * 
 * @author Christian Kaltepoth
 */
public class Sass extends StringTransformer
{

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
   public String transform(HttpServletRewrite event, String input) {
      try (SassCompiler sassCompiler = SassCompilerFactory.bundled()) {
         EmbeddedSass.OutboundMessage.CompileResponse.CompileSuccess compileSuccess = sassCompiler.compileString(input, EmbeddedSass.Syntax.SCSS);
         return compileSuccess.getCss();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }
}
