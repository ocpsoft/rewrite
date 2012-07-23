package org.ocpsoft.rewrite.servlet.wrapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.ocpsoft.common.util.Streams;
import org.ocpsoft.rewrite.servlet.config.OutputBuffer;

public class BufferedResponseToLowercase1 implements OutputBuffer
{
   @Override
   public InputStream execute(InputStream input)
   {
      String contents = Streams.toString(input);
      return new ByteArrayInputStream(contents.toLowerCase().getBytes());
   }
}
