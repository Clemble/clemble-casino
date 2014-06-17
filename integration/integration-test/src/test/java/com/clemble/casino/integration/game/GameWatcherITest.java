package com.clemble.casino.integration.game;

import static com.clemble.test.util.CollectionAssert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.clemble.casino.integration.game.construction.GameScenarios;
import com.clemble.casino.integration.spring.IntegrationTestSpringConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionAwareEvent;
import com.clemble.casino.game.outcome.PlayerWonOutcome;
import com.clemble.casino.integration.event.EventAccumulator;
import com.clemble.casino.integration.game.construction.PlayerScenarios;
import com.clemble.casino.integration.game.construction.SyncGameScenarios;
import com.clemble.casino.integration.util.RedisCleaner;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { IntegrationTestSpringConfiguration.class })
@TestExecutionListeners(listeners = { RedisCleaner.class, DependencyInjectionTestExecutionListener.class })
public class GameWatcherITest {

    @Autowired
    public PlayerScenarios playerScenarios;

    @Autowired
    public GameScenarios gameScenarios;

    @Test
    public void testSimpleScenarioObservation() {
        ClembleCasinoOperations player = playerScenarios.createPlayer();

        List<RoundGamePlayer<NumberState>> players = gameScenarios.round(Game.num);
        RoundGamePlayer A = players.get(0);
        RoundGamePlayer B = players.get(1);

        EventAccumulator<GameSessionAwareEvent> watcherListener = new EventAccumulator<GameSessionAwareEvent>();
        player.gameConstructionOperations().watch(A.getSession(), watcherListener);

        assertTrue(A.isAlive());
        assertTrue(B.isAlive());

        A.perform(new SelectNumberAction(A.getPlayer(), 2));
        B.perform(new SelectNumberAction(B.getPlayer(), 1));

        A.waitForEnd();

        assertFalse(B.isAlive());
        assertFalse(A.isAlive());

        PlayerWonOutcome wonOutcome = (PlayerWonOutcome) B.getOutcome();
        assertEquals(wonOutcome.getWinner(), A.playerOperations().getPlayer());

        assertTrue(watcherListener.toList().size() > 0);

        List<GameSessionAwareEvent> watchedEvents = watcherListener.toList();
        assertTrue(watchedEvents.size() > 0);

        assertContains(A.getEvents(), watchedEvents);
        assertContains(B.getEvents(), watchedEvents);
    }

}