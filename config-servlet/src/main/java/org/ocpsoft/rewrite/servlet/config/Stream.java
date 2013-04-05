package org.ocpsoft.rewrite.servlet.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.config.response.ResponseStreamWrapper;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Transpositions;

/**
 * An {@link Operation} responsible for streaming a {@link File} on the host file-system to the
 * {@link HttpServletResponse#getOutputStream()}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Stream extends HttpOperation implements Parameterized
{
   public static final Logger log = Logger.getLogger(Stream.class);

   // TODO TEST ME!!!
   public final static String STREAM_KEY = Stream.class.getName() + "_STREAM";

   protected RegexParameterizedPatternBuilder target;

   public Stream(File target)
   {
      this.target = new RegexParameterizedPatternBuilder(target.getAbsolutePath());
   }

   /**
    * Create an {@link Operation} that streams the given {@link File} to the
    * {@link HttpServletResponse#getOutputStream()}.
    * 
    * <p>
    * The given {@link File} path may be parameterized:
    * <p>
    * <code>
    *    new File("/tmp/file.txt") <br>
    *    new File("c:\tmp\{param}.txt") <br>
    *    ...
    * </code>
    * <p>
    * 
    * @param location {@link ParameterizedPattern} specifying the location of the {@link File}.
    * 
    * @see {@link ConfigurationRuleParameterBuilder#where(String)}
    */
   public static Stream from(File file)
   {
      return new Stream(file) {

         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            InputStream stream = null;
            try {
               File file = new File(target.build(event, context, Transpositions.encodePath()));
               stream = new BufferedInputStream(new FileInputStream(file));
               log.debug("Streaming from file [" + file + "] to response.");
               Response.write(stream).perform(event, context);
            }
            catch (Exception e) {
               throw new RewriteException("Error streaming file.", e);
            }
            finally
            {
               if (stream != null)
                  try {
                     stream.close();
                  }
                  catch (IOException e) {
                     throw new RewriteException("Error closing stream.", e);
                  }
            }
         }

         @Override
         public String toString()
         {
            return "Stream.from(" + target + ")";
         }
      };
   }

   /**
    * Create an {@link Operation} that duplicates content written to {@link HttpServletResponse#getOutputStream()} and
    * writes it to the given {@link File}.
    */
   public static Operation to(File file)
   {
      return new Stream(file) {
         @Override
         public void performHttp(HttpServletRewrite event, EvaluationContext context)
         {
            final File file = new File(target.build(event, context, Transpositions.encodePath()));
            if (!file.exists())
            {
               file.mkdirs();
               file.delete();
               try {
                  file.createNewFile();
               }
               catch (IOException e) {
                  throw new RewriteException("Could not create file for Stream operation", e);
               }
            }

            Response.withOutputStreamWrappedBy(new ResponseStreamWrapper() {

               @Override
               public OutputStream wrap(HttpServletRewrite rewrite, OutputStream outputStream)
               {
                  try {
                     BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                     rewrite.getRequest().setAttribute(STREAM_KEY, stream);
                     log.debug("Cloning response OutputStream to file [" + file + "]");
                     return new MultiOutputStream(stream, outputStream);
                  }
                  catch (FileNotFoundException e) {
                     throw new RewriteException("Could not wrap stream", e);
                  }
               }

               @Override
               public void finish(HttpServletRewrite rewrite)
               {
                  try {
                     OutputStream stream = (OutputStream) rewrite.getRequest().getAttribute(STREAM_KEY);
                     if (stream != null)
                     {
                        log.debug("Closing cloned file [" + file + "] OutputStream");
                        stream.flush();
                        stream.close();
                     }

                  }
                  catch (Exception e) {
                     throw new RewriteException("Could not close stream", e);
                  }
               }
            }).perform(event, context);
         }

         @Override
         public String toString()
         {
            return "Stream.to(" + target + ")";
         }
      };
   }

   private static class MultiOutputStream extends OutputStream
   {
      private OutputStream[] streams;

      public MultiOutputStream(OutputStream... streams)
      {
         this.streams = streams;
      }

      @Override
      public void close() throws IOException
      {
         for (int i = 0; i < streams.length; i++)
            streams[i].close();
      }

      @Override
      public void flush() throws IOException
      {
         for (int i = 0; i < streams.length; i++)
            streams[i].flush();
      }

      @Override
      public void write(int b) throws IOException
      {
         for (int i = 0; i < streams.length; i++)
            streams[i].write(b);
      }

      @Override
      public void write(byte[] b) throws IOException
      {
         for (int i = 0; i < streams.length; i++)
            streams[i].write(b);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException
      {
         for (int i = 0; i < streams.length; i++)
            streams[i].write(b, off, len);
      }
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return target.getRequiredParameterNames();
   }

   @Override
   public void setParameterStore(ParameterStore store)
   {
      target.setParameterStore(store);
   }

   public ParameterizedPatternBuilder getExpression()
   {
      return target;
   }

}
