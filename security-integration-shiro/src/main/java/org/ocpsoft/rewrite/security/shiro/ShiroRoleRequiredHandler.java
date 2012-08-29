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
package org.ocpsoft.rewrite.security.shiro;

import java.util.Arrays;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.handler.HandlerWeights;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 * @author <a href
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ShiroRoleRequiredHandler implements AnnotationHandler<ShiroRoleRequired>
{

   @Override
   public Class<ShiroRoleRequired> handles()
   {
      return ShiroRoleRequired.class;
   }

   @Override
   public int priority()
   {
      return HandlerWeights.WEIGHT_TYPE_ENRICHING;
   }

   @Override
   public void process(ClassContext context, ShiroRoleRequired annotation, HandlerChain chain)
   {
      Condition roleCondition = new ShiroRoleCondition(annotation.value());
      Condition conjunction = context.getRuleBuilder().getConditionBuilder().and(roleCondition);
      context.getRuleBuilder().when(conjunction);
      chain.proceed();
   }

   /**
    * Implementation of {@link Condition} that checks the subject's roles
    * 
    * @author Christian Kaltepoth
    */
   private static class ShiroRoleCondition implements Condition
   {

      private final Collection<String> roles;

      public ShiroRoleCondition(String[] roles)
      {
         this.roles = Arrays.asList(roles);
      }

      @Override
      public boolean evaluate(Rewrite event, EvaluationContext context)
      {
         return SecurityUtils.getSubject().hasAllRoles(roles);
      }

   }

}
