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

import java.lang.reflect.Method;

import javax.el.FunctionMapper;

/**
 * <p>
 * A dummy function mapper that is installed by default. It is not capable of
 * mapping any functions.
 * </p>
 * <p/>
 * <p>
 * Other modules (such as the Faces Module) may install a bean that
 * overrides this and provides function mapping.
 * </p>
 *
 * @author Pete Muir
 * @author Stuart Douglas
 */
@Mapper
public class DummyFunctionMapper extends FunctionMapper {

    @Override
    public Method resolveFunction(String prefix, String localName) {
        return null;
    }

}
