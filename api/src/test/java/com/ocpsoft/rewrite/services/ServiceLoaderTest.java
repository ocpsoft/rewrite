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
package com.ocpsoft.rewrite.services;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ocpsoft.rewrite.util.Iterators;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ServiceLoaderTest
{
   @Test
   @SuppressWarnings("unchecked")
   public void test()
   {
      ServiceLoader<DummyService> services = ServiceLoader.load(DummyService.class);
      List<DummyService> list = Iterators.asList(services);
      Assert.assertFalse(list.isEmpty());
   }
}
