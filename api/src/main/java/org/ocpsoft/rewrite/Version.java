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
package org.ocpsoft.rewrite;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Helper class to read the version of Rewrite
 * 
 * @author Christian Kaltepoth
 */
public class Version
{

   public static String getFullName()
   {

      StringBuilder b = new StringBuilder();
      b.append("Rewrite");

      String version = getVersion();
      if (version != null) {
         b.append(' ').append(version);
      }

      return b.toString();

   }

   public static String getVersion()
   {

      ClassLoader classLoader = Version.class.getClassLoader();

      URL url = classLoader.getResource("META-INF/maven/org.ocpsoft.rewrite/rewrite-api/pom.properties");
      if (url != null) {

         InputStream stream = null;
         try {

            stream = url.openStream();

            Properties props = new Properties();
            props.load(stream);

            return props.getProperty("version");

         }
         catch (IOException e) {
            // ignore
         }
         finally {
            if (stream != null) {
               try {
                  stream.close();
               }
               catch (IOException e) {
                  // ignore
               }
            }
         }

      }

      return null;

   }

}
