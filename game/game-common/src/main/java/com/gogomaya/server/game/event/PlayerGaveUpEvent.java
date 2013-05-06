package com.gogomaya.server.game.event;

import com.gogomaya.server.game.action.GameState;
import com.gogomaya.server.player.PlayerAware;

public class PlayerGaveUpEvent<State extends GameState> extends GameEvent<State> implements PlayerAware {

    /**
     * Generated 07/05/13
     */
    private static final long serialVersionUID = 8613548852525073195L;

    private int playerId;

    @Override
    public long getPlayerId() {
        return playerId;
    }

}