/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

import java.util.Collections;
import java.util.List;

import org.ocpsoft.common.pattern.WeightedComparator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.spi.InstanceProvider;

/**
 * Utility class for looking up instances of Java types. See also {@link InstanceProvider}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Instances
{
   private static Logger log = Logger.getLogger(Instances.class);

   private static List<InstanceProvider> instanceProviders;

   private Instances()
   {}

   /**
    * Lookup an instance of the given {@link Class} type.
    */
   @SuppressWarnings("unchecked")
   public static <T> T lookup(Class<T> type)
   {
      if (instanceProviders == null) {
         instanceProviders = Iterators.asList(ServiceLoader.load(InstanceProvider.class));
         Collections.sort(instanceProviders, new WeightedComparator());
         ServiceLogger.logLoadedServices(log, InstanceProvider.class, instanceProviders);
      }

      T result = null;
      for (InstanceProvider p : instanceProviders) {
         result = (T) p.getInstance(type);
         if (result != null)
         {
            break;
         }
      }
      return result;
   }
}
