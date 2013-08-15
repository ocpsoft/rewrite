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
package org.ocpsoft.urlbuilder;

/**
 * Represents a valid web address, or valid web address fragment.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Address
{
   /**
    * Return the anchor '#' section of the {@link Address}, or <code>null</code> if no anchor is set.
    */
   String getAnchor();

   /**
    * Return <code>true</code> if the {@link Address} contains an anchor '#' section, otherwise return
    * <code>false</code>.
    */
   boolean isAnchorSet();

   /**
    * Get the path section of this {@link Address}, or <code>null</code> if no path is set.
    */
   String getPath();

   /**
    * Get the path and query sections of this {@link Address}, or <code>null</code> if neither are set.
    */
   String getPathAndQuery();

   /**
    * Return <code>true</code> if this {@link Address} contains a path section, otherwise return <code>false</code>.
    */
   boolean isPathSet();

   /**
    * Get the port of this {@link Address}, or <code>null</code> if no port is set.
    */
   Integer getPort();

   /**
    * Return <code>true</code> if a port is set in {@link Address}, otherwise return <code>false</code>.
    */
   boolean isPortSet();

   /**
    * Get the domain section of this {@link Address}, or <code>null</code> if no domain is set.
    */
   String getDomain();

   /**
    * Return <code>true</code> if this {@link Address} has a domain section, otherwise return <code>false</code>.
    */
   boolean isDomainSet();

   /**
    * Get the scheme section of this {@link Address}, or null if no scheme is set.
    */
   String getScheme();

   /**
    * Return <code>true</code> if this {@link Address} has a scheme section, otherwise return <code>false</code>.
    */
   boolean isSchemeSet();

   /**
    * Get the scheme section of this {@link Address}, or null if no scheme specific part is set.
    */
   String getSchemeSpecificPart();

   /**
    * Return <code>true</code> if this {@link Address} has a scheme specific part section, otherwise return <code>false</code>.
    */
   boolean isSchemeSpecificPartSet();

   /**
    * Get the query section of this {@link Address}, or <code>null</code> if no query is set.
    */
   String getQuery();

   /**
    * Return <code>true</code> if this {@link Address} contains a query section, otherwise return <code>false</code>.
    */
   boolean isQuerySet();
}
