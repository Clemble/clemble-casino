package com.clemble.casino.integration.goal;

import com.clemble.casino.bet.Bid;
import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.event.EventSelector;
import com.clemble.casino.client.event.EventSelectors;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.client.event.PlayerEventSelector;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalRoleConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.NoReminderRule;
import com.clemble.casino.goal.lifecycle.construction.GoalConstruction;
import com.clemble.casino.goal.lifecycle.construction.GoalConstructionRequest;
import com.clemble.casino.goal.lifecycle.management.GoalRole;
import com.clemble.casino.integration.event.EventAccumulator;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.integration.utils.CheckUtils;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.privacy.PrivacyRule;
import com.clemble.casino.lifecycle.configuration.rule.time.MoveTimeRule;
import com.clemble.casino.lifecycle.configuration.rule.time.TotalTimeRule;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.server.event.SystemEvent;
import com.clemble.casino.server.event.email.SystemEmailSendRequestEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mavarazy on 12/31/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class GoalEmailNotificationTest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Autowired
    public EventAccumulator<SystemEvent> systemEventAccumulator;

    final private GoalConfiguration CONFIGURATION = new GoalConfiguration(
        "email:notification:test",
        "Email Notification Test",
        new Bid(Money.create(Currency.FakeMoney, 100), Money.create(Currency.FakeMoney, 50)),
        new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
        NoReminderRule.INSTANCE,
        new MoveTimeRule(TimeUnit.SECONDS.toMillis(2), LooseBreachPunishment.getInstance()),
        new TotalTimeRule(TimeUnit.SECONDS.toMillis(4), LooseBreachPunishment.getInstance()),
        PrivacyRule.world,
        new GoalRoleConfiguration(
            GoalRole.supporter,
            new Bid(Money.create(Currency.FakeMoney, 100), Money.create(Currency.FakeMoney, 50)),
            new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
            NoReminderRule.INSTANCE
        ),
        new GoalRoleConfiguration(
            GoalRole.supporter,
            new Bid(Money.create(Currency.FakeMoney, 100), Money.create(Currency.FakeMoney, 50)),
            new BasicReminderRule(TimeUnit.SECONDS.toMillis(1)),
            NoReminderRule.INSTANCE
        )
    );

    @Test
    public void testInitialized() {
        Assert.assertNotNull(playerScenarios);
        Assert.assertNotNull(systemEventAccumulator);
    }

    @Test
    public void testNotification() {
        // Step 1. Create player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        EventSelector emailSelector = EventSelectors.
            where(new PlayerEventSelector(A.getPlayer())).
            and(new EventTypeSelector(SystemEmailSendRequestEvent.class));
        // Step 2. Create construction
        GoalConstructionRequest requestA = new GoalConstructionRequest(CONFIGURATION, "Test email notification", new Date());
        A.goalOperations().constructionService().construct(requestA);
        // Step 3. Checking timeout email notification received
        SystemEmailSendRequestEvent reminderNotification = (SystemEmailSendRequestEvent) systemEventAccumulator.waitFor(emailSelector);
        Assert.assertNotNull(reminderNotification);
        Assert.assertEquals(reminderNotification.getTemplate(), "goal_due");
    }

    @Test
    public void testSupporterNotification() {
        // Step 1. Create player
        final ClembleCasinoOperations A = playerScenarios.createPlayer();
        final EventSelector AEmailSelector = EventSelectors.
            where(new PlayerEventSelector(A.getPlayer())).
            and(new EventTypeSelector(SystemEmailSendRequestEvent.class));
        // Step 2. Create supporter
        final ClembleCasinoOperations B = playerScenarios.createPlayer();
        final EventSelector BEmailSelector = EventSelectors.
            where(new PlayerEventSelector(B.getPlayer())).
            and(new EventTypeSelector(SystemEmailSendRequestEvent.class));
        // Step 3. Creating requests
        final GoalConstructionRequest requestA = new GoalConstructionRequest(CONFIGURATION, "Test email notification", new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)));
        final GoalConstruction constructionA = A.goalOperations().constructionService().construct(requestA);
        // Step 4. Checking Requests
        CheckUtils.check((i) ->
            A.goalOperations().initiationService().get(constructionA.getGoalKey()) != null
        );
        B.goalOperations().initiationService().bid(constructionA.getGoalKey(), GoalRole.supporter);
        // Step 5. Starting goal A
        A.goalOperations().initiationService().confirm(constructionA.getGoalKey());
        // Step 6. Checking value
        SystemEmailSendRequestEvent reminderNotification = (SystemEmailSendRequestEvent) systemEventAccumulator.waitFor(BEmailSelector);
        Assert.assertNotNull(reminderNotification);
        Assert.assertEquals(reminderNotification.getTemplate(), "goal_due");
    }

    @Test
    public void testObserverNotification() {
        // Step 1. Create player
        final ClembleCasinoOperations A = playerScenarios.createPlayer();
        final EventSelector AEmailSelector = EventSelectors.
            where(new PlayerEventSelector(A.getPlayer())).
            and(new EventTypeSelector(SystemEmailSendRequestEvent.class));
        // Step 2. Create supporter
        final ClembleCasinoOperations B = playerScenarios.createPlayer();
        final EventSelector BEmailSelector = EventSelectors.
            where(new PlayerEventSelector(B.getPlayer())).
            and(new EventTypeSelector(SystemEmailSendRequestEvent.class));
        // Step 3. Creating requests
        final GoalConstructionRequest requestA = new GoalConstructionRequest(CONFIGURATION, "Test email notification", new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1)));
        final GoalConstruction constructionA = A.goalOperations().constructionService().construct(requestA);
        // Step 4. Checking Requests
        CheckUtils.check((i) ->
            A.goalOperations().initiationService().get(constructionA.getGoalKey()) != null
        );
        B.goalOperations().initiationService().bid(constructionA.getGoalKey(), GoalRole.observer);
        // Step 5. Starting goal A
        A.goalOperations().initiationService().confirm(constructionA.getGoalKey());
        // Step 6. Checking value
        SystemEmailSendRequestEvent reminderNotification = (SystemEmailSendRequestEvent) systemEventAccumulator.waitFor(BEmailSelector);
        Assert.assertNotNull(reminderNotification);
        Assert.assertEquals(reminderNotification.getTemplate(), "goal_due");
    }

}