package com.gogomaya.server.integration.tictactoe;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.gogomaya.server.game.GameSpecification;
import com.gogomaya.server.game.configuration.GameSpecificationOptions;
import com.gogomaya.server.game.configuration.SelectSpecificationOptions;
import com.gogomaya.server.game.tictactoe.action.TicTacToeState;
import com.gogomaya.server.game.tictactoe.action.TicTacToeTable;
import com.gogomaya.server.integration.game.GameOperations;
import com.gogomaya.server.integration.game.listener.GameListener;
import com.gogomaya.server.integration.game.listener.GameListenerOperations;
import com.gogomaya.server.integration.player.Player;
import com.gogomaya.server.integration.player.PlayerOperations;
import com.google.common.collect.ImmutableList;

public class TicTacToeOperations {

    final private RestTemplate restTemplate;
    final private String baseUrl;

    final private PlayerOperations playerOperations;
    final private GameOperations gameOperations;
    final private GameListenerOperations<TicTacToeTable> tableListenerOperations;

    public TicTacToeOperations(String baseUrl,
            RestTemplate restTemplate,
            PlayerOperations playerOperations,
            GameOperations gameOperations,
            GameListenerOperations<TicTacToeTable> tableListenerOperations) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.gameOperations = gameOperations;
        this.playerOperations = playerOperations;
        this.tableListenerOperations = tableListenerOperations;
    }

    public List<TicTacToePlayer> start() {
        // Step 1. Selecting specification for the game
        GameSpecification specification = selectSpecification();
        // Step 2. Creating user and trying to put them on the same table
        TicTacToePlayer playerA = start(specification);
        TicTacToePlayer playerB = start(specification);
        while (playerA.getTable().getTableId() != playerB.getTable().getTableId()) {
            playerA = start(specification);
            // waits added to be sure everyone on the same page
            if (playerA.getTable().getTableId() != playerB.getTable().getTableId()) {
                playerB = start(specification);
                playerA.waitVersion(2);
            } else {
                playerB.waitVersion(2);
            }
        }
        // Step 3. Returning generated value who ever goes first is choosen as first
        TicTacToeState state = playerB.getTable().getState() != null ? playerB.getTable().getState() : playerA.getTable().getState();
        if (state.getNextMove(playerA.getPlayer().getPlayerId()) == null) {
            return ImmutableList.<TicTacToePlayer> of(playerB, playerA);
        } else {
            return ImmutableList.<TicTacToePlayer> of(playerA, playerB);
        }
    }

    public TicTacToePlayer start(GameSpecification specification) {
        // Step 1. Creating player
        Player player = playerOperations.createPlayer();
        // Step 2. Requesting table
        TicTacToeTable table = gameOperations.start(player, specification);
        final TicTacToePlayer toePlayer = new TicTacToePlayer(restTemplate, baseUrl).setPlayer(player).setTable(table);
        // Step 3. Creating listener, that will update GameListener
        tableListenerOperations.listen(table, new GameListener<TicTacToeTable>() {

            @Override
            public void updated(TicTacToeTable gameTable) {
                toePlayer.setTable(gameTable);
            }

        });
        // Step 3. Creating player configurations
        return toePlayer;
    }

    private GameSpecification selectSpecification() {
        GameSpecificationOptions specificationOptions = gameOperations.getOptions();
        if (specificationOptions instanceof SelectSpecificationOptions) {
            return ((SelectSpecificationOptions) specificationOptions).getSpecifications().get(0);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
