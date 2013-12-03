/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Utility for extracting values from the {@link ParameterValueStore}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Parameters
{
   /**
    * Retrieve a single parameter value from the {@link ParameterValueStore}.
    */
   public static String retrieve(EvaluationContext context, String string)
   {
      return ((ParameterValueStore) context.get(ParameterValueStore.class)).retrieve(((ParameterStore) context
               .get(ParameterStore.class)).get(string));
   }
}
