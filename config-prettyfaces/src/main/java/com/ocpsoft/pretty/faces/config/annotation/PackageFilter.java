/*
 * Copyright 2010 Lincoln Baxter, III
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
package com.ocpsoft.pretty.faces.config.annotation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This class represents a package filter specified by the
 * <code>com.ocpsoft.pretty.SCAN_PACKAGES</code> initialization parameter.
 * </p>
 * 
 * @author Christian Kaltepoth
 */
public class PackageFilter
{

   private final static Log log = LogFactory.getLog(PackageFilter.class);

   private final Set<String> packageSet = new HashSet<String>();

   /**
    * Creates a new {@link PackageFilter}
    * 
    * @param config
    *           Comma-separated list of packages (null safe)
    */
   public PackageFilter(String config)
   {
      // keep empty package set for empty config value
      if (config == null || config.trim().length() == 0)
      {
         if (log.isDebugEnabled())
         {
            log.debug("No package filter specified!");
         }
         return;
      }

      // split string and add all package names
      String[] names = config.split(",");
      for (String name : names)
      {
         if (name.trim().length() > 0)
         {
            packageSet.add(name.trim());
         }
      }

      if (log.isDebugEnabled())
      {
         log.debug("Number of packages in filter: " + packageSet.size());
      }

   }

   /**
    * <p>
    * Checks whether the supplied packages matches the filter.
    * </p>
    * <p>
    * The method returns <code>true</code> if one of the following checks
    * succeed:
    * </p>
    * <ul>
    * <li>The supplied package is one of the packages specified in the filter
    * condition.</li>
    * <li>The supplied package is a sub-package of one of the packages specified
    * in the filter condition.</li>
    * </ul>
    * 
    * @param packageName
    *           A package name
    * @return <code>true</code> if the filter matches
    */
   public boolean isAllowedPackage(String packageName)
   {

      // No packages in set? Accept all packages.
      if (packageSet.isEmpty())
      {
         return true;
      }

      // check all valid packages
      for (String validPackage : packageSet)
      {
         // accept if package is a sub-package of a valid package
         if (packageName.startsWith(validPackage))
         {
            return true;
         }
      }

      // package noch accepted
      return false;

   }

   /**
    * Returns the total number of base packages that this instance is holding.
    * 
    * @return Number of base packages
    */
   public int getNumberOfBasePackages()
   {
      return packageSet.size();
   }

}
