/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.param;

import org.ocpsoft.rewrite.config.Rule;

/**
 * Represents a {@link Rule} that has a {@link ParameterStore}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ParameterizedRule extends Rule, Parameterized
{
   /**
    * Return the {@link ParameterStore} for this {@link Rule}
    */
   ParameterStore getParameterStore();
}
