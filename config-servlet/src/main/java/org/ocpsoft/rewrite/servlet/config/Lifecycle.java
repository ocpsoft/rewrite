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
package org.ocpsoft.rewrite.servlet.config;

import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.OperationBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.event.ServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * An {@link Operation} responsible for controlling the {@link Rewrite} life-cycle itself.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Lifecycle extends HttpOperation
{
   /**
    * Create an {@link Operation} that calls {@link ServletRewrite#abort()}
    */
   public static OperationBuilder abort()
   {
      return new Lifecycle() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.abort();
         }

         @Override
         public String toString()
         {
            return "Lifecycle.abort()";
         }
      };
   }

   /**
    * Create an {@link Operation} that calls {@link ServletRewrite#handled()}
    */
   public static OperationBuilder handled()
   {
      return new Lifecycle() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.handled();
         }

         @Override
         public String toString()
         {
            return "Lifecycle.handled()";
         }
      };
   }

   /**
    * Create an {@link Operation} that calls {@link ServletRewrite#proceed()}
    */
   public static OperationBuilder proceed()
   {
      return new Lifecycle() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.proceed();
         }

         @Override
         public String toString()
         {
            return "Lifecycle.proceed()";
         }
      };
   }
}
