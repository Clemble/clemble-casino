package com.gogomaya.server.spring.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.BaseUriMethodArgumentResolver;

import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaValidationService;
import com.gogomaya.server.player.PlayerProfile;
import com.gogomaya.server.player.SocialConnectionData;
import com.gogomaya.server.player.security.PlayerCredential;
import com.gogomaya.server.player.security.PlayerIdentity;
import com.gogomaya.server.player.web.RegistrationRequest;
import com.gogomaya.server.spring.web.payment.PaymentWebSpringConfiguration;
import com.gogomaya.server.spring.web.player.PlayerWebSpringConfiguration;
import com.gogomaya.server.web.GenericSchemaController;

@Configuration
@Import({ CommonWebSpringConfiguration.class, TicTacToeWebSpringConfiguration.class, PlayerWebSpringConfiguration.class, PaymentWebSpringConfiguration.class })
public class WebMvcSpiConfiguration {

    @Inject
    GogomayaValidationService validationService;

    @Bean
    @Singleton
    public BaseUriMethodArgumentResolver baseUriMethodArgumentResolver() {
        return new BaseUriMethodArgumentResolver();
    }

    @Bean
    @Singleton
    public GenericSchemaController jsonSchemaController() {
        GenericSchemaController genericSchemaController = new GenericSchemaController();
        genericSchemaController.addSchemaMapping("profile", PlayerProfile.class);
        genericSchemaController.addSchemaMapping("social", SocialConnectionData.class);
        genericSchemaController.addSchemaMapping("identity", PlayerIdentity.class);
        genericSchemaController.addSchemaMapping("credentials", PlayerCredential.class);
        genericSchemaController.addSchemaMapping("registration", RegistrationRequest.class);
        genericSchemaController.addSchemaMapping("error", GogomayaError.class);
        return genericSchemaController;
    }

}
