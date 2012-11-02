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
package com.ocpsoft.pretty.faces.el;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An implementation of {@link PrettyExpression} used when the name of the bean
 * should be resolved lazily. This class is used by the annotation configuration
 * mechanism.
 * 
 * @author Christian Kaltepoth
 */
public class LazyExpression implements PrettyExpression
{

   private final static Log log = LogFactory.getLog(LazyExpression.class);

   /**
    * LazyBeanNameFinder used to resolve the bean names
    */
   private final LazyBeanNameFinder finder;

   /**
    * the bean class
    */
   private final Class<?> beanClass;

   /**
    * The component of the bean the expression refers to. Can be a single
    * property name, a property path or method name.
    */
   private final String component;

   /**
    * Holds the expression once it has been build lazily
    */
   private String expression;

   /**
    * Creates a new {@link LazyExpression}
    * 
    * @param finder
    *            The bean name finder to user for lazy resolving
    * @param beanClass
    *            the class of the bean
    * @param component
    *            the component of the bean referenced by the expression. Can be
    *            a single property name, a property path or a method name
    */
   public LazyExpression(LazyBeanNameFinder finder, Class<?> beanClass, String component)
   {
      this.finder = finder;
      this.beanClass = beanClass;
      this.component = component;
   }

   /*
    * @see com.ocpsoft.pretty.faces.expression.PrettyExpression#getELExpression()
    */
   public String getELExpression()
   {

      // build the expression if not already done
      if (expression == null)
      {

         /*
          * Build the expression. Note that findBeanName() will either
          * return the resolved bean name or throw a runtime exception
          */
         expression = "#{" + finder.findBeanName(beanClass) + "." + component + "}";

         // log the resolved expression on trace level
         if (log.isTraceEnabled())
         {
            log.trace("Lazy expression resolved to: " + expression);
         }

      }
      return expression;
   }

   @Override
   public String toString()
   {
      return "#{[" + beanClass.getName() + "]." + component + "}";
   }

   /**
    * The class this lazy expression refers to.
    * 
    * @return The class of the bean
    */
   public Class<?> getBeanClass()
   {
      return beanClass;
   }

   /**
    * The component of the bean the expression refers to.
    * 
    * @return the component of the bean referenced by the expression. Can be a
    *         single property name, a property path or a method name
    */
   public String getComponent()
   {
      return component;
   }

   /*
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((beanClass == null) ? 0 : beanClass.hashCode());
      result = prime * result + ((expression == null) ? 0 : expression.hashCode());
      return result;
   }

   /*
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      LazyExpression other = (LazyExpression) obj;
      if (beanClass == null)
      {
         if (other.beanClass != null)
         {
            return false;
         }
      }
      else if (!beanClass.equals(other.beanClass))
      {
         return false;
      }
      if (expression == null)
      {
         if (other.expression != null)
         {
            return false;
         }
      }
      else if (!expression.equals(other.expression))
      {
         return false;
      }
      return true;
   }
   
}
