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

import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * <p>
 * A dummy variable mapper that is installed by default. It is not capable of
 * mapping any variables.
 * </p>
 * <p/>
 * <p>
 * Other modules (such as the Faces Module) may install a bean that
 * overrides this and provides variable mapping.
 * </p>
 *
 * @author Stuart Douglas
 * @author Pete Muir
 */
@Mapper
public class DummyVariableMapper extends VariableMapper {
    @Override
    public ValueExpression resolveVariable(String variable) {
        return null;
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        return null;
    }

}
