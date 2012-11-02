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
package com.ocpsoft.pretty.faces.url;

import java.util.Iterator;
import java.util.List;

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

      for (Iterator<String> iter = segments.iterator(); iter.hasNext();)
      {
         String segment = iter.next();
         result.append(segment);
         if (iter.hasNext())
         {
            result.append("/");
         }
      }

      if (hasTrailingSlash())
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
