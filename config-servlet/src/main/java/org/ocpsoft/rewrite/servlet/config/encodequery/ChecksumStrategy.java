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

import org.ocpsoft.rewrite.servlet.config.EncodeQuery;

/**
 * Strategy for performing checksum embedding and validation.
 * 
 * @see EncodeQuery
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ChecksumStrategy
{
   /**
    * Return <code>true</code> if the given token contains a valid checksum.
    */
   boolean checksumValid(String token);

   /**
    * Return the given token with an additional added checksum.
    */
   String embedChecksum(String token);

   /**
    * Extract the checksum from the given token.
    */
   String removeChecksum(String token);

}
