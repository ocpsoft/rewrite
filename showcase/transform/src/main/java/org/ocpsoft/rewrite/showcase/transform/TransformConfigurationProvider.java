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
package org.ocpsoft.rewrite.showcase.transform;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Resource;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.transform.Transform;
import org.ocpsoft.rewrite.transform.less.Less;
import org.ocpsoft.rewrite.transform.markup.Asciidoc;
import org.ocpsoft.rewrite.transform.markup.Markdown;
import org.ocpsoft.rewrite.transform.markup.Sass;
import org.ocpsoft.rewrite.transform.markup.Textile;

public class TransformConfigurationProvider extends HttpConfigurationProvider
{

   @Override
   public Configuration getConfiguration(ServletContext context)
   {
      return ConfigurationBuilder.begin()

               // Markdown
               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/markdown/{name}.html"))
                        .and(Resource.exists("/markdown/{name}.md")))
               .perform(Forward.to("/markdown/{name}.md")
                        .and(Response.setContentType("text/html"))
                        .and(Transform.with(Markdown.fullDocument()
                                 .withTitle("Markdown Demo")
                                 .addStylesheet(context.getContextPath() + "/common/bootstrap.css")
                                 .addStylesheet(context.getContextPath() + "/common/common.css"))))

               // Textile
               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/textile/{name}.html"))
                        .and(Resource.exists("/textile/{name}.textile")))
               .perform(Forward.to("/textile/{name}.textile")
                        .and(Response.setContentType("text/html"))
                        .and(Transform.with(Textile.fullDocument()
                                 .withTitle("Textile Demo")
                                 .addStylesheet(context.getContextPath() + "/common/bootstrap.css")
                                 .addStylesheet(context.getContextPath() + "/common/common.css"))))

               // AsciiDoc
               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/asciidoc/{name}.html"))
                        .and(Resource.exists("/asciidoc/{name}.asciidoc")))
               .perform(Forward.to("/asciidoc/{name}.asciidoc")
                        .and(Response.setContentType("text/html"))
                        .and(Transform.with(Asciidoc.fullDocument()
                                 .withTitle("AsciiDoc Demo")
                                 .addStylesheet(context.getContextPath() + "/common/bootstrap.css")
                                 .addStylesheet(context.getContextPath() + "/common/common.css"))))

               // LESS
               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/less/{name}.css"))
                        .and(Resource.exists("/less/{name}.less")))
               .perform(Forward.to("/less/{name}.less")
                        .and(Response.setContentType("text/css"))
                        .and(Transform.with(Less.compiler())))

               // Sass
               .addRule()
               .when(Direction.isInbound()
                        .and(Path.matches("/sass/{name}.css"))
                        .and(Resource.exists("/sass/{name}.scss")))
               .perform(Forward.to("/sass/{name}.scss")
                        .and(Response.setContentType("text/css"))
                        .and(Transform.with(Sass.compiler())));

   }

   @Override
   public int priority()
   {
      return 0;
   }

}
