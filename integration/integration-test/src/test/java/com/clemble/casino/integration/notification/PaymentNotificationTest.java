package com.clemble.casino.integration.notification;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.integration.utils.CheckUtils;
import com.clemble.casino.notification.PlayerNotification;
import com.clemble.casino.payment.bonus.PaymentBonusSource;
import com.clemble.casino.payment.notification.PaymentBonusNotification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

/**
 * Created by mavarazy on 11/29/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class PaymentNotificationTest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Test
    public void checkRegistrationNotification() {
        // Step 1. Create A player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Check notification appeared
        CheckUtils.check((i) -> A.notificationService().myNotifications().length >= 1);
        // Step 3. Checking registration bonus
        boolean containsRegistrationBonus = Arrays.asList(A.notificationService().myNotifications()).stream().anyMatch((notification) -> {
            return (notification instanceof PaymentBonusNotification && ((PaymentBonusNotification) notification).getBonusSource() == PaymentBonusSource.registration);
        });
        Assert.assertTrue(containsRegistrationBonus);
    }

}