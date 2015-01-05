package com.clemble.casino.goal.spring;

import com.clemble.casino.bet.Bid;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.GoalRoleConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.BasicReminderRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.reminder.NoReminderRule;
import com.clemble.casino.goal.lifecycle.initiation.GoalInitiation;
import com.clemble.casino.goal.lifecycle.management.GoalRole;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.goal.repository.GoalRecordRepository;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.privacy.PrivacyRule;
import com.clemble.casino.lifecycle.configuration.rule.time.MoveTimeRule;
import com.clemble.casino.lifecycle.configuration.rule.time.TotalTimeRule;
import com.clemble.casino.lifecycle.configuration.rule.timeout.EODTimeoutCalculator;
import com.clemble.casino.lifecycle.configuration.rule.timeout.MoveTimeoutCalculator;
import com.clemble.casino.lifecycle.configuration.rule.timeout.TimeoutRule;
import com.clemble.casino.lifecycle.configuration.rule.timeout.TotalTimeoutCalculator;
import com.clemble.casino.lifecycle.initiation.InitiationState;
import com.clemble.casino.lifecycle.record.EventRecord;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.payment.Bank;
import com.clemble.test.concurrent.AsyncCompletionUtils;
import com.clemble.test.concurrent.Check;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by mavarazy on 16/10/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { GoalManagementSpringConfiguration.class })
public class ShortGoalManagerFactoryTest {

    @Autowired
    public GoalManagerFactoryFacade managerFactory;

    @Autowired
    public GoalRecordRepository recordRepository;

    final private GoalConfiguration configuration = new GoalConfiguration(
        "basic",
        "Basic",
        new Bid(Money.create(Currency.FakeMoney, 500), Money.create(Currency.FakeMoney, 50)),
        new BasicReminderRule(TimeUnit.HOURS.toMillis(4)),
        new BasicReminderRule(TimeUnit.HOURS.toMillis(2)),
        new TimeoutRule(LooseBreachPunishment.getInstance(), new MoveTimeoutCalculator(TimeUnit.SECONDS.toMillis(1))),
        new TimeoutRule(LooseBreachPunishment.getInstance(), new TotalTimeoutCalculator(TimeUnit.SECONDS.toMillis(3))),
        PrivacyRule.me,
        new GoalRoleConfiguration(GoalRole.supporter, new Bid(Money.create(Currency.FakeMoney, 500), Money.create(Currency.FakeMoney, 50)), NoReminderRule.INSTANCE, NoReminderRule.INSTANCE),
        new GoalRoleConfiguration(GoalRole.observer, new Bid(Money.create(Currency.FakeMoney, 500), Money.create(Currency.FakeMoney, 50)), NoReminderRule.INSTANCE, NoReminderRule.INSTANCE)
    );

    @Test
    public void testSimpleCreation() {
        // Step 1. Generating goal
        String goalKey = RandomStringUtils.randomAlphabetic(10);
        String player = RandomStringUtils.randomAlphabetic(10);
        GoalInitiation initiation = new GoalInitiation(
            goalKey,
            InitiationState.initiated,
            Bank.create(player, configuration.getBid()),
            player,
            "Create goal state",
            configuration,
            new HashSet<>(),
            new HashSet<>(),
            new Date());
        // Step 2. Starting initiation
        managerFactory.start(null, initiation);
        // Step 3. Checking there is a state for the game
        Assert.assertNotEquals(managerFactory.get(goalKey), null);
    }

    @Test
    @Ignore // TODO move this test to integration - Schedule is external service currently
    public void testSimpleTimeout() throws InterruptedException {
        // Step 1. Generating goal
        final String goalKey = RandomStringUtils.randomAlphabetic(10);
        String player = RandomStringUtils.randomAlphabetic(10);
        GoalInitiation initiation = new GoalInitiation(
                goalKey,
                InitiationState.initiated,
                Bank.create(player, configuration.getBid()),
                player,
                "Create goal state",
                configuration,
                new HashSet<>(),
                new HashSet<>(),
                new Date());
        // Step 2. Starting initiation
        managerFactory.start(null, initiation);
        // Step 3. Checking there is a state for the game
        Thread.sleep(2000);
        AsyncCompletionUtils.check(new Check() {
            @Override
            public boolean check() {
                Set<EventRecord> events = recordRepository.findOne(goalKey).getEventRecords();
                for (EventRecord record : events) {
                    if (record.getEvent() instanceof GoalEndedEvent) {
                        return true;
                    }
                }
                return false;
            }
        }, 30_000);
        AsyncCompletionUtils.check(new Check() {
            @Override
            public boolean check() {
                return managerFactory.get(goalKey) == null;
            }
        }, 1_000);
    }

}
