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
package org.ocpsoft.rewrite.config;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternParser;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternParser;
import org.ocpsoft.rewrite.util.Transforms;

/**
 * A {@link Condition} responsible for determining existence of {@link File} paths on the host file-system.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Filesystem implements Condition, Parameterized
{
   private final ParameterizedPatternParser resource;
   private final FileFilter filter;

   private Filesystem(final File resource, FileFilter filter)
   {
      this.resource = new RegexParameterizedPatternParser(resource.getAbsolutePath());
      this.filter = filter;
   }

   @Override
   public boolean evaluate(final Rewrite event, final EvaluationContext context)
   {
      if (resource != null)
      {
         String filePath = resource.getBuilder().build(event, context, Transforms.encodePath());
         File file = new File(filePath);
         return filter.accept(file);
      }
      return false;
   }

   /**
    * Create a {@link Condition} that returns <code>true</code> if the given {@link File} exists in the file-system of
    * the host environment, and is a normal file.
    * 
    * <p>
    * The given file path may be parameterized:
    * <p>
    * <code>
    *    /tmp/file.txt <br>
    *    c:\tmp\{param}.txt <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the location of the {@link File}.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static Filesystem fileExists(final File resource)
   {
      return new Filesystem(resource, new FileFilter() {
         @Override
         public boolean accept(File file)
         {
            return file.exists() && file.isFile();
         }
      });
   }

   /**
    * Create a {@link Condition} that returns <code>true</code> if the given {@link File} exists in the file-system of
    * the host environment, and is a directory.
    * 
    * <p>
    * The given file path may be parameterized:
    * <p>
    * <code>
    *    /tmp/file.txt <br>
    *    c:\tmp\{param}.txt <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the location of the directory.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static Filesystem directoryExists(final File resource)
   {
      return new Filesystem(resource, new FileFilter() {
         @Override
         public boolean accept(File file)
         {
            return file.exists() && file.isDirectory();
         }
      });
   }

   /**
    * Get the {@link ParameterizedPattern} of this {@link Filesystem}.
    */
   public ParameterizedPatternParser getExpression()
   {
      return resource;
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return resource.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      resource.setParameterStore(store);
   }

}
