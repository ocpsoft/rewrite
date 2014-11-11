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
package org.ocpsoft.rewrite.servlet.config;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.mock.MockEvaluationContext;
import org.ocpsoft.rewrite.servlet.impl.HttpInboundRewriteImpl;
import org.ocpsoft.rewrite.util.ParameterUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HeaderAndPathTest
{
    private Rewrite rewrite;
    private HttpServletRequest request;

    @Before
    public void before()
    {
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeaderNames())
                    .thenReturn(Collections.enumeration(Arrays.asList("Accept-Charset", "Content-Length")));

        Mockito.when(request.getHeaders("Content-Length"))
                    .thenReturn(Collections.enumeration(Arrays.asList("06091984")));

        Mockito.when(request.getHeaders("Accept-Charset"))
                    .thenReturn(Collections.enumeration(Arrays.asList("ISO-9965", "UTF-8")));

        Mockito.when(request.getRequestURI())
                    .thenReturn("/context/application/path");

        Mockito.when(request.getContextPath())
                    .thenReturn("/context");

        rewrite = new HttpInboundRewriteImpl(request, null, null);
    }

    @Test
    public void testHeaderAndPath()
    {
        MockEvaluationContext context = new MockEvaluationContext();
        Path path = Path.matches("/application/{seg}");
        ParameterUtils.initialize(context, path);
        Header header = Header.exists("Accept-{charset}");
        ParameterUtils.initialize(context, header);

        Assert.assertTrue(path.and(header).evaluate(rewrite, context));
    }

    @Test
    public void testHeaderAndPathDoNotMatch()
    {
        Assert.assertFalse(
                    Path.matches("/wrong-application/{path}").and(
                                Header.exists("Accept-{charset}")
                                ).evaluate(rewrite, new MockEvaluationContext()));
    }
}
