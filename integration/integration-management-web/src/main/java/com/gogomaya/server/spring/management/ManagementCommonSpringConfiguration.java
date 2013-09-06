package com.gogomaya.server.spring.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.gogomaya.configuration.GameLocation;
import com.gogomaya.configuration.ResourceLocationService;
import com.gogomaya.server.configuration.SimpleNotificationConfigurationService;
import com.gogomaya.server.configuration.SimpleResourceLocationController;
import com.gogomaya.server.player.notification.PlayerNotificationRegistry;
import com.gogomaya.server.spring.common.CommonSpringConfiguration;
import com.gogomaya.server.spring.common.SpringConfiguration;
import com.gogomaya.server.spring.payment.PaymentCommonSpringConfiguration;
import com.gogomaya.server.spring.player.PlayerCommonSpringConfiguration;
import com.google.common.collect.ImmutableList;

@Configuration
@Import(value = { CommonSpringConfiguration.class, PlayerCommonSpringConfiguration.class, PaymentCommonSpringConfiguration.class })
public class ManagementCommonSpringConfiguration implements SpringConfiguration {

    @Configuration
    @Profile({ UNIT_TEST, DEFAULT, INTEGRATION_DEFAULT })
    public static class DefaultAndTest {

        @Autowired
        public PlayerNotificationRegistry notificationRegistry;

        @Bean
        public ResourceLocationService resourceLocationService() {
            SimpleNotificationConfigurationService configurationService = new SimpleNotificationConfigurationService("guest", "guest", notificationRegistry);
            return new SimpleResourceLocationController(configurationService,
                    "http://localhost:8080/player-web/",
                    "http://localhost:8080/payment-web/",
                    ImmutableList.<GameLocation>of());
        }

    }

    @Configuration
    @Profile(INTEGRATION_TEST)
    public static class Integration {

        @Autowired
        public PlayerNotificationRegistry notificationRegistry;

        @Bean
        public ResourceLocationService resourceLocationService() {
            SimpleNotificationConfigurationService configurationService = new SimpleNotificationConfigurationService("guest", "guest", notificationRegistry);
            return new SimpleResourceLocationController(configurationService,
                    "http://localhost:9999/player-web/",
                    "http://localhost:9999/payment-web/",
                    ImmutableList.<GameLocation>of());
        }

    }

    @Configuration
    @Profile(CLOUD)
    public static class Cloud {

        @Autowired
        public PlayerNotificationRegistry notificationRegistry;

        @Bean
        public ResourceLocationService resourceLocationService() {
            SimpleNotificationConfigurationService configurationService = new SimpleNotificationConfigurationService("guest", "guest", notificationRegistry);
            return new SimpleResourceLocationController(configurationService,
                    "http://ec2-50-16-93-157.compute-1.amazonaws.com/player-web/",
                    "http://ec2-50-16-93-157.compute-1.amazonaws.com/payment-web/",
                    ImmutableList.<GameLocation>of());
        }

    }

}