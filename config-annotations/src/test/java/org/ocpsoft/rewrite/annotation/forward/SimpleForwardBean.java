package org.ocpsoft.rewrite.annotation.forward;

import org.ocpsoft.rewrite.annotation.ForwardTo;
import org.ocpsoft.rewrite.annotation.PathPattern;

@PathPattern("/forward")
@ForwardTo("/simple-forward.html")
public class SimpleForwardBean
{
}
