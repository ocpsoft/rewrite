package com.ocpsoft.rewrite.cdi.qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.ocpsoft.rewrite.cdi.config.CdiConfigurationProviderBridge;
import com.ocpsoft.rewrite.config.ConfigurationProvider;

/**
 * Qualifier used to mark a bean as a Rewrite SPI implementation. Currently this only works with {@link ConfigurationProvider}.
 * 
 * @see CdiConfigurationProviderBridge
 * @author Christian Kaltepoth
 */
@Qualifier
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RewriteComponent {

}
