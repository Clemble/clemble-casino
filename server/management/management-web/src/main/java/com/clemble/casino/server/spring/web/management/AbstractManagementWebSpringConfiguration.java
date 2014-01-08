package com.clemble.casino.server.spring.web.management;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.configuration.ResourceLocationService;
import com.clemble.casino.configuration.ServerRegistryConfiguration;
import com.clemble.casino.error.ClembleCasinoValidationService;
import com.clemble.casino.server.player.PlayerIdGenerator;
import com.clemble.casino.server.player.presence.ServerPlayerPresenceService;
import com.clemble.casino.server.player.presence.SystemNotificationService;
import com.clemble.casino.server.player.registration.ServerProfileRegistrationService;
import com.clemble.casino.server.player.security.AESPlayerTokenFactory;
import com.clemble.casino.server.player.security.PlayerTokenFactory;
import com.clemble.casino.server.repository.player.PlayerCredentialRepository;
import com.clemble.casino.server.repository.player.PlayerSessionRepository;
import com.clemble.casino.server.security.ClembleConsumerDetailsService;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.server.spring.payment.PaymentCommonSpringConfiguration;
import com.clemble.casino.server.spring.player.PlayerCommonSpringConfiguration;
import com.clemble.casino.server.spring.web.OAuthSpringConfiguration;
import com.clemble.casino.server.spring.web.WebCommonSpringConfiguration;
import com.clemble.casino.server.web.management.PlayerRegistrationController;
import com.clemble.casino.server.web.management.PlayerSessionController;

@Configuration
@Import(value = { WebCommonSpringConfiguration.class, PlayerCommonSpringConfiguration.class, PlayerJPASpringConfiguration.class,
        PaymentCommonSpringConfiguration.class, OAuthSpringConfiguration.class })
abstract public class AbstractManagementWebSpringConfiguration implements SpringConfiguration {

    @Autowired
    public ServerRegistryConfiguration paymentEndpointRegistry;

    @Bean
    public PlayerTokenFactory playerTokenFactory() throws NoSuchAlgorithmException {
        return new AESPlayerTokenFactory();
    }

    @Bean
    public PlayerRegistrationController playerRegistrationController(
            @Qualifier("playerProfileRegistrationService") ServerProfileRegistrationService playerProfileRegistrationService, PlayerIdGenerator idGenerator,
            PlayerCredentialRepository playerCredentialRepository, ClembleConsumerDetailsService clembleConsumerDetailsService,
            ClembleCasinoValidationService gogomayaValidationService, SystemNotificationService systemNotificationService) throws NoSuchAlgorithmException {
        return new PlayerRegistrationController(idGenerator, playerTokenFactory(), playerProfileRegistrationService, playerCredentialRepository,
                clembleConsumerDetailsService, gogomayaValidationService, systemNotificationService);
    }

    @Bean
    public PlayerSessionController playerSessionController(ResourceLocationService resourceLocationService, PlayerSessionRepository playerSessionRepository,
            ServerPlayerPresenceService playerStateManager) {
        return new PlayerSessionController(resourceLocationService, playerSessionRepository, playerStateManager);
    }

}
