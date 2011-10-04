package com.ocpsoft.rewrite.cdi.qualifier;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Annotation literal for {@link RewriteComponent}
 * 
 * @author Christian Kaltepoth
 */
public class RewriteComponentLiteral extends AnnotationLiteral<RewriteComponent> implements RewriteComponent {

    private static final long serialVersionUID = 1L;

    public static RewriteComponentLiteral INSTANCE = new RewriteComponentLiteral();

}
