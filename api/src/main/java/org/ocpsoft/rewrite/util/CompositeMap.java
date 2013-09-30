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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link Map} implementation supporting delegation to other {@link Map} instances configured with
 * {@link CompositeMap#addDelegate(Map)}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * @param <KEYTYPE>
 * @param <VALUETYPE>
 */
public class CompositeMap<KEYTYPE, VALUETYPE> implements Map<KEYTYPE, VALUETYPE>
{
   @SuppressWarnings("unchecked")
   private Map<KEYTYPE, VALUETYPE>[] delegates = new Map[] {};

   @SuppressWarnings("unchecked")
   public CompositeMap<KEYTYPE, VALUETYPE> addDelegate(Map<KEYTYPE, VALUETYPE> delegate)
   {
      /*
       * Highly optimized for performance reasons. Think before you change this and profile after!
       */
      List<Map<KEYTYPE, VALUETYPE>> temp = new ArrayList<Map<KEYTYPE, VALUETYPE>>(Arrays.asList(delegates));
      temp.add(delegate);

      delegates = temp.toArray(new Map[] {});
      return this;
   }

   @Override
   public void clear()
   {
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         delegate.clear();
      }
   }

   @Override
   public boolean containsKey(Object key)
   {
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         if (delegate.containsKey(key))
            return true;
      }
      return false;
   }

   @Override
   public boolean containsValue(Object value)
   {
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         if (delegate.containsValue(value))
            return true;
      }
      return false;
   }

   @Override
   public Set<java.util.Map.Entry<KEYTYPE, VALUETYPE>> entrySet()
   {
      Set<java.util.Map.Entry<KEYTYPE, VALUETYPE>> entries = new LinkedHashSet<Map.Entry<KEYTYPE, VALUETYPE>>();
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         entries.addAll(delegate.entrySet());
      }
      return entries;
   }

   @Override
   public VALUETYPE get(Object key)
   {
      VALUETYPE result = null;
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         VALUETYPE v = delegate.get(key);
         if (v != null)
         {
            result = v;
            break;
         }
      }
      return result;
   }

   @Override
   public Object clone()
   {
      LinkedHashMap<KEYTYPE, VALUETYPE> result = new LinkedHashMap<KEYTYPE, VALUETYPE>();
      result.putAll(this);
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         result.putAll(delegate);
      }
      return result;
   }

   @Override
   public boolean isEmpty()
   {
      boolean empty = true;
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         if (!delegate.isEmpty())
         {
            empty = false;
            break;
         }
      }
      return empty;
   }

   @Override
   public Set<KEYTYPE> keySet()
   {
      Set<KEYTYPE> result = new LinkedHashSet<KEYTYPE>();
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         result.addAll(delegate.keySet());
      }
      return result;
   }

   @Override
   public VALUETYPE put(KEYTYPE key, VALUETYPE value)
   {
      throw new UnsupportedOperationException("Cannot add values to composite map view.");
   }

   @Override
   public void putAll(Map<? extends KEYTYPE, ? extends VALUETYPE> map)
   {
      throw new UnsupportedOperationException("Cannot add values to composite map view.");
   }

   @Override
   public VALUETYPE remove(Object key)
   {
      throw new UnsupportedOperationException("Cannot remove values from composite map view.");
   }

   @Override
   public int size()
   {
      int size = 0;
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         size += delegate.size();
      }
      return size;
   }

   @Override
   public Collection<VALUETYPE> values()
   {
      Set<VALUETYPE> values = new LinkedHashSet<VALUETYPE>();
      for (Map<KEYTYPE, VALUETYPE> delegate : delegates) {
         values.addAll(delegate.values());
      }
      return values;
   }

}