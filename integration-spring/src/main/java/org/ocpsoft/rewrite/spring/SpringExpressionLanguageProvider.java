package org.ocpsoft.rewrite.spring;

import org.ocpsoft.rewrite.exception.UnsupportedEvaluationException;
import org.ocpsoft.rewrite.spi.ExpressionLanguageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.web.context.WebApplicationContext;

/**
 * Implementation of {@link org.ocpsoft.rewrite.spi.ExpressionLanguageProvider} for the Spring Expression Language (SpEL)
 * 
 * @author Christian Kaltepoth
 */
public class SpringExpressionLanguageProvider implements ExpressionLanguageProvider {

    /**
     * Used to parse the SpEL expressions
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    @Autowired
    private WebApplicationContext applicationContext;

    @Override
    public Object retrieveValue(String expression) throws UnsupportedEvaluationException {

        try {

            Expression exp = parser.parseExpression(expression);
            return exp.getValue(getEvaluationContext());

        } catch (SpelEvaluationException e) {
            throw new UnsupportedEvaluationException(e);
        }

    }

    @Override
    public void submitValue(String expression, Object value) throws UnsupportedEvaluationException {

        try {

            Expression exp = parser.parseExpression(expression);
            exp.setValue(getEvaluationContext(), value);

        } catch (SpelEvaluationException e) {
            throw new UnsupportedEvaluationException(e);
        }

    }

    @Override
    public Object evaluateMethodExpression(String expression) throws UnsupportedEvaluationException {

        try {

            Expression exp = parser.parseExpression(expression);
            return exp.getValue(getEvaluationContext());

        } catch (SpelEvaluationException e) {
            throw new UnsupportedEvaluationException(e);
        }

    }

    @Override
    public Object evaluateMethodExpression(String expression, Object... values) throws UnsupportedEvaluationException {
        throw new UnsupportedEvaluationException();
    }

    /**
     * Lazily initialized by {@link #getEvaluationContext()}
     */
    private EvaluationContext _evaluationContext = null;

    /**
     * Lazily creates a StandardEvaluationContext. The code has been inspired by
     * {@link StandardBeanExpressionResolver#evaluate(String, BeanExpressionContext)}
     */
    public EvaluationContext getEvaluationContext() {

        if (_evaluationContext == null) {

            // we need a ConfigurableBeanFactory to build the BeanExpressionContext
            ConfigurableBeanFactory beanFactory = null;

            // the WebApplicationContext MAY implement ConfigurableBeanFactory
            if (applicationContext instanceof ConfigurableBeanFactory) {
                beanFactory = (ConfigurableBeanFactory) applicationContext;
            }

            // the AutowireCapableBeanFactory usually implements ConfigurableListableBeanFactory
            if (beanFactory == null && applicationContext != null
                    && applicationContext.getAutowireCapableBeanFactory() instanceof ConfigurableBeanFactory) {
                beanFactory = (ConfigurableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            }

            // we cannot continue without a ConfigurableBeanFactory
            if (beanFactory == null) {
                throw new IllegalStateException("Unable to find a ConfigurableBeanFactory");
            }

            BeanExpressionContext beanEvaluationContext = new BeanExpressionContext(beanFactory, null);

            StandardEvaluationContext sec = new StandardEvaluationContext();
            sec.setRootObject(beanEvaluationContext);
            sec.addPropertyAccessor(new BeanExpressionContextAccessor());
            sec.addPropertyAccessor(new BeanFactoryAccessor());
            sec.addPropertyAccessor(new MapAccessor());
            sec.setBeanResolver(new BeanFactoryResolver(beanEvaluationContext.getBeanFactory()));
            sec.setTypeLocator(new StandardTypeLocator(beanEvaluationContext.getBeanFactory().getBeanClassLoader()));
            ConversionService conversionService = beanEvaluationContext.getBeanFactory().getConversionService();
            if (conversionService != null) {
                sec.setTypeConverter(new StandardTypeConverter(conversionService));
            }

            _evaluationContext = sec;
        }

        return _evaluationContext;

    }

}
