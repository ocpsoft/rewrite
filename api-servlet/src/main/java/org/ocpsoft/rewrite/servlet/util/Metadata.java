/*
 * Copyright 2010 Lincoln Baxter, III
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
package org.ocpsoft.rewrite.servlet.util;

import java.util.List;

import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * @deprecated Use {@link AddressBuilder} instead. May be removed in subsequent releases.
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Deprecated
public class Metadata
{
   private String encoding = "UTF-8";
   private boolean trailingSlash = false;
   private boolean leadingSlash = false;

   /**
    * Return a copy of this Metadata
    */
   public Metadata copy()
   {
      Metadata result = new Metadata();
      result.setEncoding(encoding);
      result.setTrailingSlash(trailingSlash);
      result.setLeadingSlash(leadingSlash);
      return result;
   }

   public String buildURLFromSegments(final List<String> segments)
   {
      StringBuilder result = new StringBuilder();

      if (hasLeadingSlash())
      {
         result.append("/");
      }

      /*
       * Normal loop for performance reasons
       */
      for (int i = 0; i < segments.size(); i++) {
         String segment = segments.get(i);
         result.append(segment);
         if (i < (segments.size() - 1))
         {
            result.append("/");
         }
      }

      if (hasTrailingSlash() && (!segments.isEmpty() || (result.indexOf("/") < 0)))
      {
         result.append("/");
      }
      return result.toString();
   }

   /*
    * Getters & Setters
    */

   public String getEncoding()
   {
      return encoding;
   }

   public void setEncoding(final String encoding)
   {
      this.encoding = encoding;
   }

   public boolean hasTrailingSlash()
   {
      return trailingSlash;
   }

   public void setTrailingSlash(final boolean trailingSlash)
   {
      this.trailingSlash = trailingSlash;
   }

   public boolean hasLeadingSlash()
   {
      return leadingSlash;
   }

   public void setLeadingSlash(final boolean leadingSlash)
   {
      this.leadingSlash = leadingSlash;
   }
}
