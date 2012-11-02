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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.mapping.QueryParameter;
import com.ocpsoft.pretty.faces.config.mapping.RequestParameter;
import com.ocpsoft.pretty.faces.el.ConstantExpression;

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

        assertNotSame(param1.hashCode(), param3.hashCode());
        assertNotSame(param1.hashCode(), param4.hashCode());
        assertNotSame(param3.hashCode(), param4.hashCode());
        assertEquals(param1.hashCode(), param2.hashCode());

        assertNotSame(param1, param3);
        assertNotSame(param1, param4);
        assertNotSame(param3, param4);
        assertEquals(param1, param2);
    }

    @Test
    public void testQueryParameterStringString()
    {
        QueryParameter param1 = new QueryParameter("foo", "bar");
        assertEquals("foo", param1.getName());
        assertEquals("bar", param1.getValue());
    }

    @Test
    public void testQueryParameterStringStringString()
    {
        QueryParameter param1 = new QueryParameter("name", "value", new ConstantExpression("expression"));
        assertEquals("name", param1.getName());
        assertEquals("value", param1.getValue());
        assertEquals("expression", param1.getExpression().getELExpression());
    }
}
