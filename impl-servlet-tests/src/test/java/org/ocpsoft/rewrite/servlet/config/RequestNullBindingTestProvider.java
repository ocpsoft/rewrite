/*
 * Copyright 2011 <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
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
package org.ocpsoft.rewrite.servlet.config;

import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Converter;

/**
 * @author <a href="mailto:fabmars@gmail.com">Fabien Marsaud</a>
 */
public class RequestNullBindingTestProvider extends HttpConfigurationProvider
{
  @Override
  public int priority() {
    return 0;
  }
 
  @Override
  public Configuration getConfiguration(final ServletContext context)
  {
     Configuration config = ConfigurationBuilder
              .begin()

              .addRule()
              .when(Direction.isInbound().and(Path.matches("/foo/{id}")))
              .perform(SendStatus.code(200))
              .where("id").bindsTo(Evaluation.property("id")).matches(".*")
              .convertedBy(longConverter)

              .addRule()
              .when(Direction.isInbound().and(Path.matches("/bar/{id}/")))
              .perform(SendStatus.code(200))
              .where("id").bindsTo(Evaluation.property("id")).matches(".*")
              .convertedBy(longConverter);

     return config;
  }

  private final Converter<Long> longConverter = new Converter<Long>() {
    
    @Override
    public Long convert(final Rewrite event, final EvaluationContext context, final Object value) {
      String sVal = (String)value;
      if (sVal == null || sVal.length() == 0) {
        return null;
      }
      else {
        return Long.valueOf(sVal);
      }
    }
  };
}
