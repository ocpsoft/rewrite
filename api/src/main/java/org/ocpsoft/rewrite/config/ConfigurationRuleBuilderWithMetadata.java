/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.rewrite.config;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public interface ConfigurationRuleBuilderWithMetadata extends ConfigurationBuilderRoot
{
   /**
    * Specify additional meta-data to be associated with this {@link Rule}. Meta-data does not directly affect a
    * {@link Rule}'s behavior, but can be used to provide hints to custom extensions.
    */
   ConfigurationRuleBuilderWithMetadata withMetadata(Object key, Object value);
}
