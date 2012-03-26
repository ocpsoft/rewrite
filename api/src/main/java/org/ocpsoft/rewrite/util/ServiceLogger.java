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
package org.ocpsoft.rewrite.util;

import java.util.List;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.logging.Logger;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class ServiceLogger
{

   public static <T> void logLoadedServices(final Logger log, final Class<T> type, final List<? extends T> services)
   {
      log.info("Loaded [" + services.size() + "] " + type.getName() + " ["
               + joinTypeNames(services) + "]");
   }

   private static String joinTypeNames(final List<?> list)
   {
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < list.size(); i++)
      {
         Object service = list.get(i);
         result.append(service.getClass().getName());
         if (service instanceof Weighted)
         {
            result.append("<" + ((Weighted) service).priority() + ">");
         }
         if ((i + 1) < list.size())
         {
            result.append(", ");
         }
      }
      return result.toString();
   }
}
