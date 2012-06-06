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

import java.util.Arrays;

import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.transform.StringTransformer;

public class Sass extends StringTransformer {

    @Override
    public String transform(String input) {

        StringBuilder script = new StringBuilder();
        script.append("require 'sass'\n");
        script.append("engine = Sass::Engine.new('");
        script.append(input);
        script.append("', :syntax => :scss, :cache => false)\n");
        script.append("engine.render");

        // prepare the scripting environment
        ScriptingContainer container = new ScriptingContainer();
        container.setLoadPaths(Arrays.asList("ruby/sass/lib"));

        // run the transformation and return the result
        Object result = container.runScriptlet(script.toString());
        if (result != null) {
            return result.toString();
        }
        return null;

    }

}
