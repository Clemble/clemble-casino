package com.gogomaya.server.spring.web.payment;

import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.gogomaya.server.payment.PaymentTransactionService;
import com.gogomaya.server.player.account.PlayerAccountService;
import com.gogomaya.server.repository.payment.PaymentTransactionRepository;
import com.gogomaya.server.repository.player.PlayerAccountRepository;
import com.gogomaya.server.spring.common.SpringConfiguration;
import com.gogomaya.server.spring.payment.PaymentManagementSpringConfiguration;
import com.gogomaya.server.spring.web.SwaggerSpringConfiguration;
import com.gogomaya.server.spring.web.WebCommonSpringConfiguration;
import com.gogomaya.server.web.payment.PaymentTransactionController;
import com.gogomaya.server.web.player.account.PlayerAccountController;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.configuration.DefaultConfigurationModule;
import com.mangofactory.swagger.configuration.ExtensibilityModule;

@Configuration
@Import({
    PaymentWebSpringConfiguration.PaymentDefaultAndTest.class,
    PaymentManagementSpringConfiguration.class,
    WebCommonSpringConfiguration.class })
public class PaymentWebSpringConfiguration implements SpringConfiguration {

    @Autowired
    @Qualifier("realPlayerAccountService")
    public PlayerAccountService playerAccountService;

    @Autowired
    @Qualifier("paymentTransactionRepository")
    public PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    @Qualifier("realPaymentTransactionService")
    public PaymentTransactionService paymentTransactionService;

    @Autowired
    @Qualifier("playerAccountRepository")
    public PlayerAccountRepository playerAccountRepository;

    @Bean
    @Singleton
    public PaymentTransactionController paymentTransactionController() {
        return new PaymentTransactionController(paymentTransactionRepository, paymentTransactionService);
    }

    @Bean
    @Singleton
    public PlayerAccountController playerAccountController() {
        return new PlayerAccountController(playerAccountService, playerAccountRepository, paymentTransactionRepository);
    }

    @Configuration
    @Profile(value = { DEFAULT, UNIT_TEST, INTEGRATION_TEST })
    public static class PaymentDefaultAndTest extends SwaggerSpringConfiguration {

        @Override
        public SwaggerConfiguration swaggerConfiguration(DefaultConfigurationModule defaultConfig, ExtensibilityModule extensibility) {
            SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration("1.0", "http://localhost:8080/payment-web/");
            return extensibility.apply(defaultConfig.apply(swaggerConfiguration));
        }

    }

}