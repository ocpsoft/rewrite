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
package com.ocpsoft.pretty.faces.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ocpsoft.pretty.PrettyException;
import com.ocpsoft.pretty.faces.util.HTTPDecoder;

/**
 * @author lb3
 */
public class HTTPDecoderTest
{
    HTTPDecoder decoder = new HTTPDecoder();

    @Test
    public void testDecodeValidInputReturnsDecodedInput()
    {
        String value = "foo+bar";
        assertEquals("foo bar", decoder.decode(value));
    }

    @Test(expected = PrettyException.class)
    public void testDecodeInvalidInputThrowsException()
    {
        String value = "foo+bar%";
        assertEquals("foo+bar%", decoder.decode(value));
    }

    @Test
    public void testDecodeNullProducesNull()
    {
        String value = null;
        assertEquals(null, decoder.decode(value));
    }

}
