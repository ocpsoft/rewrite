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
package org.ocpsoft.rewrite.exception;

import org.ocpsoft.rewrite.event.Rewrite;

/**
 * Exception thrown by the {@link Rewrite} framework.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class RewriteException extends RuntimeException
{
   private static final long serialVersionUID = -6610549860707315081L;

   public RewriteException(final String message, final Throwable e)
   {
      super(message, e);
   }

   public RewriteException(final String message)
   {
      super(message);
   }
}
