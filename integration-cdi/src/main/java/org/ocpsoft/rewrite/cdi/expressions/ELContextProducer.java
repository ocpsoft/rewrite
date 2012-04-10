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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Responsible for creating and exposing the ELContext
 *
 * @author Stuart Douglas
 * @author Pete Muir
 */
class ELContextProducer {

    @Inject
    @Mapper
    private Instance<FunctionMapper> functionMapper;

    @Inject
    @Mapper
    private Instance<VariableMapper> variableMapper;

    @Inject
    @Composite
    private ELResolver resolver;

   @Produces
   @Composite
    ELContext createELContext() {
        return createELContext(resolver, functionMapper.get(), variableMapper.get());
    }

    private ELContext createELContext(final ELResolver resolver, final FunctionMapper functionMapper, final VariableMapper variableMapper) {
        return new ELContext() {
            @Override
            public ELResolver getELResolver() {
                return resolver;
            }

            @Override
            public FunctionMapper getFunctionMapper() {
                return functionMapper;
            }

            @Override
            public VariableMapper getVariableMapper() {
                return variableMapper;
            }
        };
    }
}
