package org.ocpsoft.rewrite.servlet.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.ocpsoft.rewrite.config.ConfigurationRuleParameterBuilder;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.ParameterizedPattern;
import org.ocpsoft.rewrite.param.ParameterizedPatternBuilder;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.util.Transforms;

/**
 * An {@link Operation} responsible for streaming a {@link File} on the host file-system to the
 * {@link HttpServletResponse#getOutputStream()}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class Stream extends HttpOperation implements Parameterized
{
   // TODO TEST ME!!!
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
               File cacheFile = new File(target.build(event, context, Transforms.encodePath()));
               stream = new BufferedInputStream(new FileInputStream(cacheFile));
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

      };
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
