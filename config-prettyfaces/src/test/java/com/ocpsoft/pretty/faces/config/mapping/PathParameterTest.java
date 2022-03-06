/*
 * Copyright 2010 Lincoln Baxter, III
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.pretty.faces.config.mapping;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.PathParameter;

import static org.assertj.core.api.Assertions.assertThat;

public class PathParameterTest
{
    @Test
    public void testIsNamedFalseWhenNameNull()
    {
        PathParameter parameter = new PathParameter();
        assertThat(parameter.isNamed()).isFalse();
    }

    @Test
    public void testIsNamedFalseWhenNameEmpty()
    {
        PathParameter parameter = new PathParameter();
        parameter.setName("");
        assertThat(parameter.isNamed()).isFalse();
    }

    @Test
    public void testIsNamedTrueWhenNameSet()
    {
        PathParameter parameter = new PathParameter();
        parameter.setName("name");
        assertThat(parameter.isNamed()).isTrue();
    }
}
