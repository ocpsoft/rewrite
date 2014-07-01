/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.config;

import org.ocpsoft.rewrite.context.Context;

/**
 * Defines a set of constants for accessing {@link Rule} metadata. Metadata will be available on {@link Rule} instances
 * that implement {@link Context}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RuleMetadata
{
   /**
    * The location where the {@link Rule} was added to the {@link ConfigurationBuilder}.
    */
   static String PROVIDER_LOCATION = RuleMetadata.class.getName() + "_LOCATION";
}
