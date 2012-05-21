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
package org.ocpsoft.rewrite.less;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.ocpsoft.rewrite.render.StringRenderer;

public class Less extends StringRenderer
{

   private final String baseScript;

   public Less()
   {
      StringBuilder scriptBuilder = new StringBuilder();
      scriptBuilder.append("function print(s) {}"); // required by env.rhino
      scriptBuilder.append(getClasspathResourceAsString("env.rhino.1.2.js"));
      scriptBuilder.append(getClasspathResourceAsString("less-1.3.0.min.js"));
      scriptBuilder.append(getClasspathResourceAsString("api.js"));
      baseScript = scriptBuilder.toString();
   }

   @Override
   public String defaultFileType()
   {
      return "css";
   }

   @Override
   public String render(String less)
   {

      Context context = Context.enter();

      try {

         context.setOptimizationLevel(-1);
         context.setLanguageVersion(Context.VERSION_1_8);

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
      try {
         InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
         if (inputStream == null) {
            throw new IllegalStateException("Could not find resource on the classpath: " + resource);
         }
         return IOUtils.toString(inputStream, Charset.forName("UTF-8"));
      }
      catch (IOException e) {
         throw new IllegalArgumentException(e);
      }
   }

   private String escape(String s)
   {
      return s.replace("\r", "").replace("\n", "\\n");
   }

}
