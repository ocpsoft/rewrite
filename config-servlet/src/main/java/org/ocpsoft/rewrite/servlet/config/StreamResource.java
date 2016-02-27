package org.ocpsoft.rewrite.servlet.config;

import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.exception.RewriteException;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.RegexParameterizedPatternBuilder;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Set;

/**
 * Allow operation for stream resource file to response
 * @author Francois Robert
 */
public abstract class StreamResource extends HttpOperation implements Parameterized {

  public static final Logger log = Logger.getLogger(StreamResource.class);

  private final String resourceLocation;
  protected RegexParameterizedPatternBuilder target;

  public StreamResource(String resourceLocation) {
    this.target = new RegexParameterizedPatternBuilder(resourceLocation);
    this.resourceLocation = resourceLocation;
  }

  /**
   * Initialize response with file from resource
   * @param resourceLocation resource location
   * @return StreamResource (HttpOperation)
   */
  public static StreamResource from(final String resourceLocation) {
    return new StreamResource(resourceLocation) {
      @Override
      public void performHttp(HttpServletRewrite httpServletRewrite, EvaluationContext evaluationContext) {
        BufferedInputStream bufferedInputStream = null;
        try {
          bufferedInputStream = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(resourceLocation));
          Response.write(bufferedInputStream).perform(httpServletRewrite, evaluationContext);
        } finally {
          if (bufferedInputStream != null) {
            try {
              bufferedInputStream.close();
            } catch (IOException e) {
              throw new RewriteException("Error closing stream.", e);
            }
          }
        }
      }
    };
  }

  @Override
  public Set<String> getRequiredParameterNames() {
    return target.getRequiredParameterNames();
  }

  @Override
  public void setParameterStore(ParameterStore store) {
    target.setParameterStore(store);
  }
}
