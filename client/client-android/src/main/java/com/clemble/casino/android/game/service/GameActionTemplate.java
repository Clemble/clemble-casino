package com.clemble.casino.android.game.service;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import com.clemble.casino.client.event.EventListener;
import com.clemble.casino.client.event.EventListenerOperations;
import com.clemble.casino.client.event.SessionEventSelector;
import com.clemble.casino.client.game.GameActionOperations;
import com.clemble.casino.event.ClientEvent;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.event.client.MadeMove;
import com.clemble.casino.game.service.GameActionService;

public class GameActionTemplate<State extends GameState> implements GameActionOperations<State> {

    private static final long serialVersionUID = -2263303118851762598L;

    final private String player;
    final private GameSessionKey session;
    final private GameActionService<State> gameActionService;
    final private EventListenerOperations eventListenersManager;

    public GameActionTemplate(String player, GameSessionKey session, EventListenerOperations eventListenersManager, GameActionService<State> gameActionService) {
        this.player = player;
        this.session = session;
        this.eventListenersManager = checkNotNull(eventListenersManager);
        this.gameActionService = checkNotNull(gameActionService);
    }

    @Override
    public State process(ClientEvent move) {
        return gameActionService.process(session.getSession(), move);
    }

    @Override
    public MadeMove getAction(int actionId) {
        return gameActionService.getAction(session.getSession(), actionId);
    }

    @Override
    public void subscribe(EventListener eventListener) {
        eventListenersManager.subscribe(new SessionEventSelector(session), eventListener);
    }

    @Override
    public GameSessionKey getSession() {
        return session;
    }

    @Override
    public String getPlayer() {
        return player;
    }

}
