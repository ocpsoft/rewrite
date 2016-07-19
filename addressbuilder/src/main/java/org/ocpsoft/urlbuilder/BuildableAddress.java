/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.ocpsoft.urlbuilder;

/**
 * Represents an object that can build and return an {@link Address} as a result.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface BuildableAddress
{
   /**
    * Generate an {@link Address} representing the current state of this {@link AddressBuilder}.
    */
   Address build();

   /**
    * Generate an {@link Address} representing the current literal state of this {@link AddressBuilder}.
    * <p>
    * (Does not apply parameterization. E.g. The URL `/{foo}` will be treated as literal text, as opposed to calling
    * {@link #build()}, which would result in `foo` being treated as a parameterized expression)
    */
   public Address buildLiteral();
}
