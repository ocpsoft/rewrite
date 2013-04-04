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
package org.ocpsoft.rewrite.transform.less;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.ocpsoft.common.util.Streams;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.transform.StringTransformer;
import org.ocpsoft.rewrite.transform.Transformer;

/**
 * A {@link Transformer} that compiles LESS files into CSS. This implementation is based on Mozilla Rhino and LESS
 * 1.3.0.
 * 
 * @author Christian Kaltepoth
 */
public class Less extends StringTransformer
{

   private final String baseScript;

   /**
    * Create a {@link Transformer} that compiles LESS files into CSS.
    */
   public static Less compiler()
   {
      return new Less();
   }

   private Less()
   {
      StringBuilder scriptBuilder = new StringBuilder();
      scriptBuilder.append("function print(s) {}"); // required by env.rhino
      scriptBuilder.append(getClasspathResourceAsString("org/ocpsoft/rewrite/transform/js/" + "env.rhino.1.2.js"));
      scriptBuilder.append(getClasspathResourceAsString("org/ocpsoft/rewrite/transform/js/" + "less-1.3.0.min.js"));
      scriptBuilder.append(getClasspathResourceAsString("org/ocpsoft/rewrite/transform/js/" + "api.js"));
      baseScript = scriptBuilder.toString();
   }

   @Override
   public String transform(HttpServletRewrite event, String less)
   {

      Context context = Context.enter();

      try {

         context.setOptimizationLevel(-1);
         context.setLanguageVersion(Context.VERSION_1_6);

         Scriptable scope = context.initStandardObjects();

         String script = new StringBuilder(baseScript)
                  .append("lessToCss('")
                  .append(escape(less))
                  .append("');")
                  .toString();

         Object result = context.evaluateString(scope, script, this.getClass().getSimpleName(), 1, null);

         if (result != null) {
            return result.toString();
         }

      }
      finally {
         Context.exit();
      }
      return null;

   }

   private static String getClasspathResourceAsString(String resource)
   {
      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
      if (input == null) {
         throw new IllegalStateException("Could not find resource on the classpath: " + resource);
      }
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Streams.copy(input, output);
      return new String(output.toByteArray(), Charset.forName("UTF-8"));
   }

   private String escape(String s)
   {
      return s.replace("\r", "").replace("\n", "\\n");
   }

}
