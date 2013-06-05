package com.gogomaya.server.game.action;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaException;
import com.gogomaya.server.game.action.move.GameMove;
import com.gogomaya.server.game.action.move.GiveUpMove;
import com.gogomaya.server.game.active.ActivePlayerQueue;
import com.gogomaya.server.game.event.GameEvent;
import com.gogomaya.server.game.notification.GameNotificationService;
import com.gogomaya.server.game.outcome.GameOutcomeService;

public class GameSessionProcessor<State extends GameState> {

    final private GameCacheService<State> cacheService;
    final private GameNotificationService<State> notificationService;
    final private GameOutcomeService<State> outcomeService;
    final private ActivePlayerQueue activePlayerQueue;

    public GameSessionProcessor(final GameOutcomeService<State> outcomeService,
            final GameCacheService<State> cacheService,
            final GameNotificationService<State> notificationService,
            final ActivePlayerQueue activePlayerQueue) {
        this.notificationService = checkNotNull(notificationService);
        this.cacheService = checkNotNull(cacheService);
        this.outcomeService = checkNotNull(outcomeService);
        this.activePlayerQueue = checkNotNull(activePlayerQueue);
    }

    public State process(long sessionId, GameMove move) {
        // Step 1. Sanity check
        if (move == null)
            throw GogomayaException.create(GogomayaError.GamePlayMoveUndefined);
        // Step 2. Acquiring lock for session event processing
        GameCache<State> cache = cacheService.get(sessionId);
        if (cache.getSession().getSessionState() == GameSessionState.inactive) {
            if (!(move instanceof GiveUpMove)) {
                throw GogomayaException.create(GogomayaError.GamePlayGameNotStarted);
            }
        }
        if (cache.getSession().getSessionState() == GameSessionState.ended) {
            if (!(move instanceof GiveUpMove)) {
                throw GogomayaException.create(GogomayaError.GamePlayGameEnded);
            }
            return cache.getSession().getState();
        }
        ReentrantLock reentrantLock = cache.getSessionLock();
        // Step 3. Acquiring lock for the session, to exclude parallel processing
        reentrantLock.lock();
        try {
            // Step 4. Recreating state if needed
            State state = cache.getState();
            // Step 5. Retrieving game processor based on session identifier
            GameProcessor<State> processor = cache.getProcessor();
            // Step 6. Processing movement
            Collection<GameEvent<State>> events = processor.process(state, move);
            for (GameEvent<State> event : events)
                event.setSession(sessionId);
            if (state.complete()) {
                cache.getSession().setSessionState(GameSessionState.ended);
                activePlayerQueue.markInActive(cache.getSession().getPlayers());
                if (state.getOutcome() instanceof PlayerWonOutcome) {
                    outcomeService.finished(cache.getSession());
                }
            }
            // Step 7. Invoking appropriate notification
            notificationService.notify(cache.getPlayerIds(), events);
            // Step 8. Returning state of the game
            return state;
        } finally {
            reentrantLock.unlock();
        }
    }

}