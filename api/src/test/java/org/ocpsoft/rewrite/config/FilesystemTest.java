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
import java.io.IOException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ocpsoft.rewrite.MockEvaluationContext;
import org.ocpsoft.rewrite.MockRewrite;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.Parameterized;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class FilesystemTest
{
   private MockRewrite event;
   private EvaluationContext context;

   public static void initialize(ParameterStore store, Parameterized parameterized)
   {
      Set<String> names = parameterized.getRequiredParameterNames();
      for (String name : names)
      {
         store.get(name, new DefaultParameter(name));
      }

      parameterized.setParameterStore(store);
   }

   @Before
   public void before()
   {
      event = new MockRewrite();
      context = new MockEvaluationContext();
      context.put(ParameterStore.class, new DefaultParameterStore());
      context.put(ParameterValueStore.class, new DefaultParameterValueStore());
   }

   @Test
   public void testWindowsFilePathsNoParams() throws IOException
   {
      String separator = System.getProperty("file.separator");
      try {
         System.setProperty("file.separator", "\\");
         File tempFile = File.createTempFile("FilesystemTest", "temp");
         String pattern = tempFile.getAbsolutePath().replaceAll("/", "\\\\");
         Filesystem filesystem = Filesystem.fileExists(new File(pattern));

         initialize(DefaultParameterStore.getInstance(context), filesystem);

         tempFile.delete();
         Assert.assertFalse(filesystem.evaluate(event, context));
         tempFile.createNewFile();
         Assert.assertTrue(filesystem.evaluate(event, context));
      }
      finally {
         System.setProperty("file.separator", separator);
      }
   }
}
