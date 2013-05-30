package com.gogomaya.server.integration.emulator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gogomaya.server.game.action.GameState;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.integration.game.GameOperations;
import com.gogomaya.server.integration.game.GamePlayer;

public class PlayerEmulator<State extends GameState> implements Runnable {

    final private Logger logger = LoggerFactory.getLogger(PlayerEmulator.class);

    final private GameSpecification specification;

    final private GameOperations<State> gameOperations;

    final private GameActor<State> actor;

    final private AtomicBoolean continueEmulation = new AtomicBoolean(true);

    final private AtomicLong lastMoved = new AtomicLong();

    final private AtomicReference<GamePlayer<State>> currentPlayer = new AtomicReference<GamePlayer<State>>();

    public PlayerEmulator(final GameActor<State> actor, final GameOperations<State> gameOperations, final GameSpecification specification) {
        this.specification = checkNotNull(specification);
        this.gameOperations = checkNotNull(gameOperations);
        this.actor = checkNotNull(actor);
    }

    public GameSpecification getSpecification() {
        return specification;
    }

    @Override
    public void run() {
        while (continueEmulation.get()) {
            // Step 1. Start player emulator
            GamePlayer<State> playerState = gameOperations.start(specification);
            logger.info("Registered {} on {} with session {}", new Object[] { playerState.getPlayer().getPlayerId(), playerState.getTable().getTableId(),
                    playerState.getSessionId() });
            currentPlayer.set(playerState);
            lastMoved.set(System.currentTimeMillis());
            while (!playerState.getState().complete()) {
                // Step 2. Waiting for player turn
                playerState.waitForTurn();
                // Step 3. Performing action
                actor.move(playerState);
            }
        }
    }

    public boolean isAlive() {
        return true;
    }

    public boolean isActive() {
        return !currentPlayer.get().getState().getNextMoves().isEmpty();
    }

    public void stop() {
        this.continueEmulation.set(false);
        this.currentPlayer.get().clear();
    }

    public long getLastMoved() {
        return lastMoved.get();
    }
}
