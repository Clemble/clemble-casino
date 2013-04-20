package com.gogomaya.integration.tictactoe;

import java.util.List;

import javax.inject.Inject;

import org.jbehave.core.annotations.UsingSteps;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gogomaya.server.integration.tictactoe.TicTacToeOperations;
import com.gogomaya.server.integration.tictactoe.TicTacToePlayer;
import com.gogomaya.server.spring.integration.IntegrationTestConfiguration;
import com.gogomaya.tests.validation.PlayerCredentialsValidation;

@RunWith(SpringJUnit4ClassRunner.class)
@UsingSteps(instances = PlayerCredentialsValidation.class)
@ContextConfiguration(classes = { IntegrationTestConfiguration.class })
public class SimpleTicTacToeGameTest {

    @Inject
    TicTacToeOperations ticTacToeOperations;

    @Test
    @Repeat(100)
    public void testSimpleStart() {
        List<TicTacToePlayer> players = ticTacToeOperations.start();
        TicTacToePlayer playerA = players.get(0);
        TicTacToePlayer playerB = players.get(1);

        int gamePrice = playerA.getTable().getSpecification().getBetRule().getPrice();

        long playerAidentifier = playerA.getPlayer().getPlayerId();
        long playerBidentifier = playerB.getPlayer().getPlayerId();

        Assert.assertNotNull(players);
        Assert.assertEquals(playerA.getTable().getTableId(), playerB.getTable().getTableId());
        Assert.assertNotNull(players.iterator().next());
        Assert.assertEquals(players.size(), 2);

        playerA.select(0, 0);

        playerA.bet(5);
        playerB.bet(2);

        Assert.assertTrue(playerB.getTable().getState().getBoard()[0][0].owned());
        Assert.assertEquals(playerB.getTable().getState().getNextMoves().size(), 1);
        Assert.assertEquals(playerB.getTable().getState().getPlayerState(playerAidentifier).getMoneyLeft(), gamePrice - 5);
        Assert.assertEquals(playerB.getTable().getState().getPlayerState(playerBidentifier).getMoneyLeft(), gamePrice - 2);
        Assert.assertNotNull(playerB.getTable().getState().getNextMove(playerBidentifier));
    }

    @Test
    @Repeat(100)
    public void testSimpleScenario() {
        List<TicTacToePlayer> players = ticTacToeOperations.start();
        TicTacToePlayer playerA = players.get(0);
        TicTacToePlayer playerB = players.get(1);

        playerA.select(0, 0);

        playerA.bet(2);
        playerB.bet(1);

        playerA.select(1, 1);

        playerA.bet(2);
        playerB.bet(1);

        playerA.select(2, 2);

        playerA.bet(2);
        playerB.bet(1);

        Assert.assertTrue(playerB.getTable().getState().complete());
    }

}
