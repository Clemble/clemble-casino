package com.clemble.casino.server.spring.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import com.clemble.casino.payment.money.Currency;
import com.clemble.casino.payment.money.Money;
import com.clemble.casino.server.payment.BasicServerPaymentTransactionService;
import com.clemble.casino.server.payment.ServerPaymentTransactionService;
import com.clemble.casino.server.payment.bonus.BonusService;
import com.clemble.casino.server.payment.bonus.DailyBonusService;
import com.clemble.casino.server.payment.bonus.PlayerConnectionDiscoveryBonusService;
import com.clemble.casino.server.payment.bonus.PlayerRegisterationBonusService;
import com.clemble.casino.server.payment.bonus.policy.BonusPolicy;
import com.clemble.casino.server.payment.bonus.policy.NoBonusPolicy;
import com.clemble.casino.server.player.account.BasicServerPlayerAccountService;
import com.clemble.casino.server.player.account.ServerPlayerAccountService;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.clemble.casino.server.player.presence.SystemNotificationServiceListener;
import com.clemble.casino.server.repository.payment.PaymentTransactionRepository;
import com.clemble.casino.server.repository.payment.PlayerAccountTemplate;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.common.PlayerPresenceSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;

@Configuration
@Import({ CommonSpringConfiguration.class, PaymentJPASpringConfiguration.class, PlayerPresenceSpringConfiguration.class })
public class PaymentManagementSpringConfiguration implements SpringConfiguration {

    @Bean
    public PlayerAccountTemplate playerAccountTemplate(JdbcTemplate jdbcTemplate) {
        return new PlayerAccountTemplate(jdbcTemplate);
    }

    @Bean
    public ServerPlayerAccountService realPlayerAccountService(PlayerAccountTemplate playerAccountRepository) {
        return new BasicServerPlayerAccountService(playerAccountRepository);
    }

    @Bean
    public ServerPaymentTransactionService realPaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository,
            PlayerNotificationService playerNotificationService, PlayerAccountTemplate accountTemplate) {
        return new BasicServerPaymentTransactionService(paymentTransactionRepository, accountTemplate, playerNotificationService);
    }

    @Bean
    public BonusService bonusService(@Qualifier("playerNotificationService") PlayerNotificationService notificationService, PlayerAccountTemplate accountTemplate,
            PaymentTransactionRepository transactionRepository, @Qualifier("realPaymentTransactionService") ServerPaymentTransactionService transactionService,
            BonusPolicy bonusPolicy) {
        return new BonusService(bonusPolicy, notificationService, accountTemplate, transactionRepository, transactionService);
    }

    @Bean
    public DailyBonusService dailyBonusService(BonusService bonusService, SystemNotificationServiceListener notificationServiceListener) {
        Money bonus = new Money(Currency.FakeMoney, 50);
        DailyBonusService dailyBonusService = new DailyBonusService(bonus, bonusService);
        notificationServiceListener.subscribe(dailyBonusService);
        return dailyBonusService;
    }

    @Bean
    public PlayerConnectionDiscoveryBonusService dicoveryBonusService(BonusService bonusService, SystemNotificationServiceListener notificationServiceListener) {
        Money bonus = new Money(Currency.FakeMoney, 100);
        PlayerConnectionDiscoveryBonusService discoveryBonusService = new PlayerConnectionDiscoveryBonusService(bonus, bonusService);
        notificationServiceListener.subscribe(discoveryBonusService);
        return discoveryBonusService;
    }

    @Bean
    public PlayerRegisterationBonusService registerationBonusService(BonusService bonusService, SystemNotificationServiceListener notificationServiceListener) {
        Money bonus = new Money(Currency.FakeMoney, 200);
        PlayerRegisterationBonusService registrationBonusService = new PlayerRegisterationBonusService(bonus, bonusService);
        notificationServiceListener.subscribe(registrationBonusService);
        return registrationBonusService;
    }

    @Bean
    public BonusPolicy bonusPolicy() {
        return new NoBonusPolicy();
    }

}
