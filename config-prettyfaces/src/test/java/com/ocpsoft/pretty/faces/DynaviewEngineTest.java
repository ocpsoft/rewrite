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
package com.ocpsoft.pretty.faces;

import org.junit.Test;

import com.ocpsoft.pretty.faces.config.dynaview.DynaviewEngine;

import static org.assertj.core.api.Assertions.assertThat;

public class DynaviewEngineTest
{
    private final DynaviewEngine dynaview = new DynaviewEngine();

    @Test
    public void testDefaultMapping() throws Exception
    {
        String mapping = "/";
        String viewId = dynaview.buildDynaViewId(mapping);
        assertThat(viewId).isEqualTo("/" + DynaviewEngine.DYNAVIEW + ".jsf");
    }

    @Test
    public void testExtensionMapping() throws Exception
    {
        String mapping = "*.faces";
        String viewId = dynaview.buildDynaViewId(mapping);
        assertThat(viewId).isEqualTo("/" + DynaviewEngine.DYNAVIEW + ".faces");
    }

    @Test
    public void testPathMapping() throws Exception
    {
        String mapping = "/faces/*";
        String viewId = dynaview.buildDynaViewId(mapping);
        assertThat(viewId).isEqualTo("/faces/" + DynaviewEngine.DYNAVIEW + ".jsf");
    }
}
