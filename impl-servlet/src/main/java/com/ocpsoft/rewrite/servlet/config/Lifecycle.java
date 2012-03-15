package com.ocpsoft.rewrite.servlet.config;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.servlet.event.ServletRewrite;
import com.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Responsible for controlling the Rewrite life-cycle itself.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Lifecycle extends HttpOperation
{
   /**
    * Calls {@link ServletRewrite#abort()}
    */
   public static Operation abort()
   {
      return new Lifecycle() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.abort();
         }
      };
   }

   /**
    * Calls {@link ServletRewrite#handled()}
    */
   public static Operation handled()
   {
      return new Lifecycle() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.handled();
         }
      };
   }

   /**
    * Calls {@link ServletRewrite#proceed()}
    */
   public static Operation proceed()
   {
      return new Lifecycle() {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            event.proceed();
         }
      };
   }
}
