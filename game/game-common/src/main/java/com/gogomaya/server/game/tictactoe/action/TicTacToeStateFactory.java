package com.gogomaya.server.game.tictactoe.action;

import java.util.HashSet;
import java.util.Set;

import com.gogomaya.server.game.GameSpecification;
import com.gogomaya.server.game.action.GamePlayerIterator;
import com.gogomaya.server.game.action.GamePlayerState;
import com.gogomaya.server.game.action.GameStateFactory;
import com.gogomaya.server.game.rule.bet.FixedBetRule;
import com.gogomaya.server.game.tictactoe.TicTacToePlayerIterator;
import com.gogomaya.server.game.tictactoe.action.move.TicTacToeSelectCellMove;

public class TicTacToeStateFactory implements GameStateFactory<TicTacToeState> {

    @Override
    public TicTacToeState initialize(final GameSpecification gameSpecification, final Set<Long> playerIds) {
        // Step 0. Create initial state
        if (gameSpecification == null)
            throw new IllegalArgumentException("Game specification can't be null");
        if (playerIds == null || playerIds.size() == 0)
            throw new IllegalArgumentException("Players can't be null or empty");
        // Step 1. Generating initial specification
        if (!(gameSpecification.getBetRule() instanceof FixedBetRule))
            throw new IllegalArgumentException("BetRule must be FixedBetRule");
        // Step 2. Create fixed bet rule
        FixedBetRule fixedBetRule = (FixedBetRule) gameSpecification.getBetRule();
        long price = fixedBetRule.getPrice();
        Set<GamePlayerState> playerStates = new HashSet<GamePlayerState>();
        for (Long playerId : playerIds) {
            playerStates.add(new GamePlayerState(playerId, price));
        }
        GamePlayerIterator playerIterator = new TicTacToePlayerIterator(0, playerStates);
        // Step 3. Initializing next player
        return (TicTacToeState) new TicTacToeState()
            .setPlayerStates(playerStates)
            .setPlayerIterator(playerIterator)
            .setNextMove(new TicTacToeSelectCellMove(playerIterator.next()))
            .incrementVersion();
    }

}
