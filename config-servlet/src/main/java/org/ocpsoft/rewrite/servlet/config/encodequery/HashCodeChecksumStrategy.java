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
package org.ocpsoft.rewrite.servlet.config.encodequery;

/**
 * {@link ChecksumStrategy} based on Java {@link String#hashCode()} equality
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HashCodeChecksumStrategy implements ChecksumStrategy
{
   private static final String CHECKSUM_DELIM = "#";

   @Override
   public boolean checksumValid(final String token)
   {
      if (token.contains(CHECKSUM_DELIM))
      {
         int hashCode = token.substring(token.indexOf(CHECKSUM_DELIM) + 1).hashCode();
         Integer storedHashCode;
         try {
            storedHashCode = Integer.valueOf(token.substring(0, token.indexOf(CHECKSUM_DELIM)));
         }
         catch (NumberFormatException e) {
            return false;
         }
         return hashCode == storedHashCode;
      }
      return false;
   }

   @Override
   public String embedChecksum(final String token)
   {
      String result = token.hashCode() + CHECKSUM_DELIM + token;
      return result;
   }

   @Override
   public String removeChecksum(final String token)
   {
      String result = token.substring(token.indexOf(CHECKSUM_DELIM) + 1);
      return result;
   }

}
