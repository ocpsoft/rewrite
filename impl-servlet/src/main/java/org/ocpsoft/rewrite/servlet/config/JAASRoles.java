package org.ocpsoft.rewrite.servlet.config;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 * Implementation of {@link Condition} that checks the subject's roles using
 * {@link HttpServletRequest#isUserInRole(String)}
 *
 * @author Christian Kaltepoth
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JAASRoles extends HttpCondition
{
   private final Collection<String> roles;

   /**
    * Create a new {@link JAASRoles} condition requiring the given roles in order for evaluation to return true.
    */
   public static JAASRoles required(String... roles)
   {
      return new JAASRoles(roles);
   }

   private JAASRoles(String[] roles)
   {
      this.roles = Arrays.asList(roles);
   }

   @Override
   public boolean evaluateHttp(HttpServletRewrite event, EvaluationContext context)
   {
      HttpServletRewrite rewrite = event;

      // check if user has all required roles
      for (String role : roles) {
         if (!rewrite.getRequest().isUserInRole(role)) {
            return false;
         }
      }

      return true;
   }
}