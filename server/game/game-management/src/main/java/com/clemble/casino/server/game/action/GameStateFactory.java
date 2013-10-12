package com.clemble.casino.server.game.action;

import com.clemble.casino.game.GameSession;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameInitiation;

public interface GameStateFactory<State extends GameState> {

    public State constructState(final GameInitiation initiation);

    public State constructState(final GameSession<State> gameSession);

}