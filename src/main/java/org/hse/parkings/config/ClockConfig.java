package org.hse.parkings.config;

import org.hse.parkings.utils.DateTimeProvider;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class ClockConfig {

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return DateTimeProvider.getInstance();
    }

    @Bean
    public Clock getClock() {
        return DateTimeProvider.getInstance().getClock();
    }

    @Bean
    public LocalValidatorFactoryBean defaultValidator(Clock clock) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean() {

            @Override
            protected void postProcessConfiguration(javax.validation.Configuration<?> configuration) {
                configuration.clockProvider(() -> clock);
            }
        };
        MessageInterpolatorFactory interpolatorFactory =
                new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        return factoryBean;
    }
}
