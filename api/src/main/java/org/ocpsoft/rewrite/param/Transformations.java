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
package org.ocpsoft.rewrite.param;

import java.net.URI;
import java.net.URISyntaxException;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * This class provides common implementations of the {@link Transform} interface.
 * 
 * @author Christian Kaltepoth
 */
public class Transformations
{

   public static Transform<String> encodePath()
   {
      return new Transform<String>() {
         @Override
         public String transform(Rewrite event, EvaluationContext context, String value)
         {
            try
            {
               final URI uri = new URI("http", "localhost", "/" + value, null);
               return uri.toASCIIString().substring(17);
            }
            catch (URISyntaxException e)
            {
               throw new IllegalArgumentException(e);
            }
         }
      };
   }

}
