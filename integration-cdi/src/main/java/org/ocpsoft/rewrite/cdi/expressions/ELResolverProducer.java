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
package org.ocpsoft.rewrite.cdi.expressions;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * <p>
 * Creates the composite EL Resolver which contains the default EL resolvers,
 * the CDI EL resolver and any user registered resolvers.
 * </p>
 *
 * @author Stuart Douglas
 */
public class ELResolverProducer {

    @Produces
    @Composite
    public ELResolver getELResolver(@Resolver Instance<ELResolver> resolvers, BeanManager beanManager) {

        // Create the default el resolvers
        CompositeELResolver compositeResolver = new CompositeELResolver();
        compositeResolver.add(beanManager.getELResolver());
        compositeResolver.add(new MapELResolver());
        compositeResolver.add(new ListELResolver());
        compositeResolver.add(new ArrayELResolver());
        compositeResolver.add(new ResourceBundleELResolver());
        compositeResolver.add(new BeanELResolver());

        boolean isGlassFish = System.getProperty("glassfish.version") != null;
        if (isGlassFish) {
            for (ELResolver resolver : getReferences(beanManager, ELResolver.class, new ResolverLiteral())) {
                compositeResolver.add(resolver);
            }
        } else {
            for (ELResolver resolver : resolvers) {
                compositeResolver.add(resolver);
            }
        }

        return compositeResolver;
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> getReferences(final BeanManager manager, final Class<T> type, Annotation... qualifiers) {
        Set<Bean<?>> resolverBeans = manager.getBeans(type, qualifiers);
        if (resolverBeans.size() == 0) {
            return Collections.emptySet();
        }
        Set<T> refs = new LinkedHashSet<T>();
        for (Bean<?> bean : resolverBeans) {
            // FIXME when should the dependent context be cleaned up?
            CreationalContext<T> context = (CreationalContext<T>) manager.createCreationalContext(bean);
            if (context != null) {
                refs.add((T) manager.getReference(bean, type, context));
            }
        }
        return refs;
    }

}
