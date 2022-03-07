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

import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.RequestParameter;
import com.ocpsoft.pretty.faces.el.ConstantExpression;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author lb3
 */
public class QueryParameterTest
{
    @Test
    public void testHashCodeAndEquals()
    {
        RequestParameter param1 = new QueryParameter("foo", "bar");
        RequestParameter param2 = new QueryParameter("foo", "bar");
        RequestParameter param3 = new QueryParameter("foo", "bar2");
        RequestParameter param4 = new QueryParameter("foo2", "bar");

        assertThat(param3.hashCode()).isNotSameAs(param1.hashCode());
        assertThat(param4.hashCode()).isNotSameAs(param1.hashCode());
        assertThat(param4.hashCode()).isNotSameAs(param3.hashCode());
        assertThat(param2.hashCode()).isEqualTo(param1.hashCode());

        assertThat(param3).isNotSameAs(param1);
        assertThat(param4).isNotSameAs(param1);
        assertThat(param4).isNotSameAs(param3);
        assertThat(param2).isEqualTo(param1);
    }

    @Test
    public void testQueryParameterStringString()
    {
        QueryParameter param1 = new QueryParameter("foo", "bar");
        assertThat(param1.getName()).isEqualTo("foo");
        assertThat(param1.getValue()).isEqualTo("bar");
    }

    @Test
    public void testQueryParameterStringStringString()
    {
        QueryParameter param1 = new QueryParameter("name", "value", new ConstantExpression("expression"));
        assertThat(param1.getName()).isEqualTo("name");
        assertThat(param1.getValue()).isEqualTo("value");
        assertThat(param1.getExpression().getELExpression()).isEqualTo("expression");
    }
}
