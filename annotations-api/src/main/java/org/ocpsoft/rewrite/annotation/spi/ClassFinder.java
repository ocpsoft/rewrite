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
package org.ocpsoft.rewrite.annotation.spi;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.rewrite.annotation.api.ClassVisitor;

/**
 * Common interface for implementations that scan for classes on the classpath.
 * 
 * @author Christian Kaltepoth
 */
public interface ClassFinder extends Weighted
{
   /**
    * Starting to search for classes. The supplied {@link ClassVisitor} must be called for every class found.
    * 
    * @param visitor The visitor to call on classes found
    */
   public void findClasses(ClassVisitor visitor);
}