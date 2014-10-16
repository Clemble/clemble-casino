package com.clemble.casino.goal.spring;

import com.clemble.casino.bet.Bid;
import com.clemble.casino.goal.action.GoalManagerFactoryFacade;
import com.clemble.casino.goal.action.SelfGoalManagerFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.configuration.rule.judge.JudgeRule;
import com.clemble.casino.goal.lifecycle.configuration.rule.judge.JudgeType;
import com.clemble.casino.goal.lifecycle.configuration.rule.parts.GoalPartsRule;
import com.clemble.casino.goal.lifecycle.initiation.GoalInitiation;
import com.clemble.casino.lifecycle.configuration.rule.bet.LimitedBetRule;
import com.clemble.casino.lifecycle.configuration.rule.breach.LooseBreachPunishment;
import com.clemble.casino.lifecycle.configuration.rule.privacy.PrivacyRule;
import com.clemble.casino.lifecycle.configuration.rule.time.MoveTimeRule;
import com.clemble.casino.lifecycle.configuration.rule.time.TotalTimeRule;
import com.clemble.casino.lifecycle.initiation.InitiationState;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.RandomStringUtils;
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
 * Created by mavarazy on 16/10/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { GoalManagementSpringConfiguration.class })
public class SelfGoalManagerFactoryTest {

    @Autowired
    public GoalManagerFactoryFacade managerFactory;

    final private GoalConfiguration configuration = new GoalConfiguration(
        "basic",
        new Bid(Money.create(Currency.FakeMoney, 50), Money.create(Currency.FakeMoney, 5)),
        LimitedBetRule.create(5, 50),
        new JudgeRule("me", JudgeType.self),
        new GoalPartsRule(1),
        new MoveTimeRule(TimeUnit.MINUTES.toMillis(1), LooseBreachPunishment.getInstance()),
        new TotalTimeRule(TimeUnit.MINUTES.toMillis(2), LooseBreachPunishment.getInstance()),
        PrivacyRule.players
    );

    @Test
    public void testSimpleCreation() {
        // Step 1. Generating goal
        String goalKey = RandomStringUtils.randomAlphabetic(10);
        String player = RandomStringUtils.randomAlphabetic(10);
        GoalInitiation initiation = new GoalInitiation(
            goalKey,
            InitiationState.initiated,
            player,
            "Create goal state",
            player,
            configuration,
            new Date());
        // Step 2. Starting initiation
        managerFactory.start(null, initiation);
        // Step 3. Checking there is a state for the game
        Assert.assertNotEquals(managerFactory.get(goalKey), null);
    }

}