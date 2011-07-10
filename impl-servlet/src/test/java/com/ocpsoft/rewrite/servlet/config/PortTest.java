/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.ocpsoft.rewrite.servlet.config;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.servlet.http.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PortTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = EasyMock.createNiceMock(HttpServletRequest.class);
      EasyMock.expect(request.getServerPort())
               .andReturn(8080).anyTimes();

      EasyMock.replay(request);

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testPortMatches()
   {
      Assert.assertTrue(Port.is(8080).evaluate(rewrite));
   }

   @Test
   public void testMultiPortMatches()
   {
      Assert.assertTrue(Port.is(8080, 9090).evaluate(rewrite));
   }

   @Test
   public void testMultiPortDoesNotMatch()
   {
      Assert.assertFalse(Port.is(9080, 9090).evaluate(rewrite));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsException()
   {
      Port.is(0).evaluate(rewrite);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsExceptionWithMultiPort()
   {
      Port.is(8080, 0).evaluate(rewrite);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testOutOfRangePortThrowsExceptionWithMultiPort2()
   {
      Port.is(0, 8080).evaluate(rewrite);
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Port.is(9090).evaluate(rewrite));
   }
}
