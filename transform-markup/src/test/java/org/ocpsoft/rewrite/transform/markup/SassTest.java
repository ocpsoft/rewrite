/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.transform.markup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SassTest {

    @Test
    public void testCalculations() {

        String sass = ".class { width: 1+1 }";
        String css = Sass.transformer().transform(sass);

        assertEquals(".class { width: 2; }", normalize(css));

    }

    @Test
    public void testNesting() {

        String sass = ".outer { margin: 2px; .inner { padding: 3px; } }";
        String css = Sass.transformer().transform(sass);

        assertEquals(".outer { margin: 2px; } .outer .inner { padding: 3px; }", normalize(css));

    }

    @Test
    public void testVariables() {

        String sass = "$mycolor: #123456; .class { color: $mycolor }";
        String css = Sass.transformer().transform(sass);

        assertEquals(".class { color: #123456; }", normalize(css));

    }

    @Test
    public void testMixins() {

        String sass = "@mixin invalid { color: red } .label { @include invalid }";
        String css = Sass.transformer().transform(sass);

        assertEquals(".label { color: red; }", normalize(css));

    }

    private static String normalize(String s) {
        return s.replaceAll("\n", "").replaceAll("[\t ]+", " ").trim();
    }

}
