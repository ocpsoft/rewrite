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

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.ocpsoft.rewrite.transform.StringTransformer;

public class Markdown extends StringTransformer {

    private final boolean completeDocument;

    public Markdown() {
        this(true);
    }

    public Markdown(boolean completeDocument) {
        this.completeDocument = completeDocument;
    }

    @Override
    public String transform(String input) {

        // the script for rendering
        StringBuilder script = new StringBuilder();
        script.append("require 'maruku'\n");
        script.append("doc = Maruku.new('");
        script.append(input);
        script.append("')\n");
        if (completeDocument) {
            script.append("doc.to_html_document");
        } else {
            script.append("doc.to_html");
        }

        ScriptingContainer container = null;
        try {
            
            // prepare the scripting environment
            container = new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.TRANSIENT);
            container.setLoadPaths(Arrays.asList("ruby/maruku/lib"));

            // run the transformation and return the result
            Object result = container.runScriptlet(script.toString());
            if (result != null) {
                return result.toString();
            }
            return null;

        } finally {
            if (container != null) {
                container.terminate();
            }
        }

    }

}
