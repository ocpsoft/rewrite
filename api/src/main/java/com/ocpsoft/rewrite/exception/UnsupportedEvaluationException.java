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
package com.ocpsoft.rewrite.exception;

import com.ocpsoft.rewrite.spi.ExpressionLanguageProvider;

/**
 * Thrown when an {@link ExpressionLanguageProvider} cannot handle an expression.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class UnsupportedEvaluationException extends RewriteException
{
   private static final long serialVersionUID = -5809070573028785614L;

   public UnsupportedEvaluationException()
   {
      super();
   }

   public UnsupportedEvaluationException(final String message, final Throwable e)
   {
      super(message, e);
   }

   public UnsupportedEvaluationException(final String message)
   {
      super(message);
   }

   public UnsupportedEvaluationException(final Throwable e)
   {
      super(e);
   }

}
