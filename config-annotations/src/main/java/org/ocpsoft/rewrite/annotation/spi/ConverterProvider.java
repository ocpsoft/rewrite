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
package org.ocpsoft.rewrite.annotation.spi;

import org.ocpsoft.rewrite.bind.Converter;

/**
 * SPI for providing integration with other conversion frameworks. Implementations should be able to build an Rewrite
 * {@link Converter} from any kind of 3rd party class.
 * 
 * @author Christian Kaltepoth
 */
public interface ConverterProvider
{

   /**
    * Create a Rewrite {@link Converter} from the given converter class. Which types are supported is up to the
    * implementation class. A JSF implementation would for example support JSF converters.
    */
   Converter<?> getByType(Class<?> converterClass);
}
