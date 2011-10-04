package com.ocpsoft.rewrite.cdi.config;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.solder.beanManager.BeanManagerLocator;
import org.jboss.seam.solder.core.Veto;

import com.ocpsoft.logging.Logger;
import com.ocpsoft.rewrite.cdi.qualifier.RewriteComponent;
import com.ocpsoft.rewrite.cdi.qualifier.RewriteComponentLiteral;
import com.ocpsoft.rewrite.config.Configuration;
import com.ocpsoft.rewrite.config.ConfigurationProvider;

/**
 * 
 * This class implements a bridge that allows Rewrite to find CDI-managed implementations of {@link ConfigurationProvider}.
 * 
 * @author Christian Kaltepoth
 */
@Veto
public class CdiConfigurationProviderBridge<E> implements ConfigurationProvider<E> {

    private static final Logger logger = Logger.getLogger(CdiConfigurationProviderBridge.class);

    private ConfigurationProvider<E> delegate;

    private boolean initialized = false;

    @Override
    public int priority() {
        tryToFindDelegate();
        if (delegate != null) {
            return delegate.priority();
        }
        return 0;
    }

    @Override
    public boolean handles(Object context) {
        tryToFindDelegate();
        if (delegate != null) {
            return delegate.handles(context);
        }
        return false;
    }

    @Override
    public Configuration getConfiguration(E context) {
        tryToFindDelegate();
        return delegate.getConfiguration(context);
    }

    /**
     * This method searches for CDI managed {@link ConfigurationProvider} implementations. Please note that this method relies
     * on Solder's {@link BeanManagerLocator} to obtain the {@link BeanManager}.
     */
    private void tryToFindDelegate() {

        // the lookup is only done once
        if (!initialized) {

            try {

                // use BeanManagerLocator for the BeanManager lookup
                BeanManager manager = new BeanManagerLocator().getBeanManager();

                if (manager != null) {

                    // find all ConfigurationProvider implementations with the @RewriteComponent qualifier found
                    Set<Bean<?>> beans = manager.getBeans(ConfigurationProvider.class, RewriteComponentLiteral.INSTANCE);

                    // we cannot continue if we did find any beans
                    if (beans.size() > 0) {

                        if (beans.size() == 1) {

                            // get an instance of the ConfigurationProvider
                            Bean<ConfigurationProvider<E>> bean = (Bean<ConfigurationProvider<E>>) beans.iterator().next();
                            delegate = bean.create(manager.createCreationalContext(bean));

                            logger.info("Found CDI-managed ConfigurationProvider: " + delegate.getClass().getName());

                        } else {

                            // build comma-separated list of bean types
                            StringBuilder beanNameList = new StringBuilder();
                            for (Bean<?> bean : beans) {
                                if (beanNameList.length() > 0) {
                                    beanNameList.append(", ");
                                }
                                beanNameList.append(bean.getBeanClass().getName());
                            }

                            logger.warn("More than one ConfigurationProvider with qualifier @"
                                    + RewriteComponent.class.getSimpleName() + " found: " + beanNameList);

                        }

                    } else {
                        logger.debug("No ConfigurationProvider implementations managed by CDI found! Did you forget to add the @"
                                + RewriteComponent.class.getSimpleName() + " qualifier?");
                    }

                } else {
                    logger.warn("BeanManagerLocator is unable to find the BeanManager.");
                }

            } finally {
                // this method should only run once
                initialized = true;
            }
        }
    }

}
