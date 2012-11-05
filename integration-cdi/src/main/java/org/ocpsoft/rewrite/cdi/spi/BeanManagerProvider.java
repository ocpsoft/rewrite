/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.cdi.spi;

import javax.enterprise.inject.spi.BeanManager;

import org.ocpsoft.common.pattern.Weighted;
import org.ocpsoft.common.services.NonEnriching;
import org.ocpsoft.rewrite.cdi.manager.BeanManagerLocator;
import org.ocpsoft.rewrite.cdi.manager.DefaultJndiBeanManagerProvider;
import org.ocpsoft.rewrite.cdi.manager.ServletContainerJndiBeanManagerProvider;

/**
 * <p>
 * {@link BeanManagerProvider} is the SPI for {@link BeanManagerLocator} which allows third parties to register a way of
 * obtaining the {@link BeanManager} outside of CDI managed objects.
 * </p>
 * <p/>
 * <p>
 * Solder provides a number of built in providers, including:
 * </p>
 * <p/>
 * <ul>
 * <li>{@link DefaultJndiBeanManagerProvider}</li>
 * <li>{@link JBossJndiBeanManagerProvider}</li>
 * <li>{@link ServletContainerJndiBeanManagerProvider}</li>
 * </ul>
 * <p/>
 * <p>
 * Providers can specify a precedence, allowing a provider to be a "last resort" provider only.
 * </p>
 * <p/>
 * <p>
 * Precedence about 100 is suggested for providers that should always be used. Precedence below 10 is suggested for
 * providers of last resort.
 * </p>
 * 
 * @author Nicklas Karlsson
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface BeanManagerProvider extends Weighted, NonEnriching
{
   /**
    * Try to obtain a BeanManager
    * 
    * @return The BeanManager (or null if non found at this location)
    */
   public abstract BeanManager getBeanManager();
}
