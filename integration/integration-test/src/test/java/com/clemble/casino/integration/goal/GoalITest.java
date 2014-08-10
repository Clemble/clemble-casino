package com.clemble.casino.integration.goal;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.goal.Goal;
import com.clemble.casino.goal.GoalState;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import com.clemble.casino.integration.util.ClembleCasinoExceptionMatcherFactory;
import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.test.random.ObjectGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

/**
 * Created by mavarazy on 8/2/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
public class GoalITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAddGoal() {
        // Step 1. Creating Player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating random goal
        check(A, "GOAL_1", A.getPlayer());
    }

    @Test
    public void testAddGoalWithoutGoalId() {
        // Step 1. Generating Player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating goal without ID
        check(A, null, A.getPlayer());
    }

    @Test
    public void testAddGoalWithoutGoalAndPlayerId() {
        // Step 1. Generating Player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating goal without ID
        check(A, null, null);
    }

    @Test
    public void testAddGoalWithAnotherPlayer() {
        // Step 1. Creating Player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating random goal
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GoalPlayerIncorrect));
        check(A, "GOAL_1", ObjectGenerator.generate(String.class));
    }

    @Test
    public void testAddGoalWithoutGoalIdWithAnotherPlayer() {
        // Step 1. Generating Player
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 2. Creating goal without ID
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GoalPlayerIncorrect));
        check(A, null, ObjectGenerator.generate(String.class));
    }

    @Test
    public void testSavingInMissedState() {
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 1. Creating random goal
        Goal goalToSave = new Goal(null, null, "Pending A goal", new Date(System.currentTimeMillis() + 10_000), GoalState.missed);
        // Step 3. Saving and checking goal state is valid
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GoalStateIncorrect));
        // Step 4. Trying to save new value
        A.goalOperations().addMyGoal(goalToSave);
    }

    @Test
    public void testSavingInReachedState() {
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 1. Creating random goal
        Goal goalToSave = new Goal(null, null, "Pending A goal", new Date(System.currentTimeMillis()), GoalState.reached);
        // Step 3. Saving and checking goal state is valid
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GoalStateIncorrect));
        // Step 4. Trying to save new value
        A.goalOperations().addMyGoal(goalToSave);
    }

    @Test
    public void testSavingWithPassedDueDate() {
        ClembleCasinoOperations A = playerScenarios.createPlayer();
        // Step 1. Creating random goal
        Goal goalToSave = new Goal(null, null, "Pending A goal", new Date(0), GoalState.pending);
        // Step 3. Saving and checking goal state is valid
        expectedException.expect(ClembleCasinoExceptionMatcherFactory.fromErrors(ClembleCasinoError.GoalDueDateInPast));
        // Step 4. Trying to save new value
        A.goalOperations().addMyGoal(goalToSave);
    }



    public void check(ClembleCasinoOperations A, String goalKey, String player) {
        // Step 1. Creating random goal
        Goal goalToSave = new Goal(player, goalKey, "Pending A goal", new Date(System.currentTimeMillis() + 10_000), GoalState.pending);
        // Step 3. Saving and checking goal is valid
        Goal savedGoal = A.goalOperations().addMyGoal(goalToSave);
        assertNotNull(savedGoal);
        assertEquals(savedGoal.getPlayer(), A.getPlayer());
        assertNotEquals(savedGoal.getGoal(), goalKey);
        assertEquals(savedGoal, goalToSave.cloneWithPlayerAndGoal(savedGoal.getPlayer(), savedGoal.getGoal(), GoalState.pending));
        // Step 4. Checking GoalOperations return goal
        assertEquals(A.goalOperations().myGoals().size(), 1);
        Goal resGoal = A.goalOperations().myGoals().iterator().next();
        assertNotNull(resGoal);
        assertEquals(resGoal.getPlayer(), A.getPlayer());
        assertEquals(resGoal, savedGoal);
    }

}
