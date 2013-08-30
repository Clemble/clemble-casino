package com.gogomaya.server.game.event.schedule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gogomaya.player.PlayerAware;
import com.gogomaya.server.event.GameConstructionEvent;

@JsonTypeName("added")
public class PlayerAddedEvent implements GameConstructionEvent, PlayerAware {

    /**
     * Generated 10/06/13
     */
    private static final long serialVersionUID = -5442563161615470910L;

    final private long session;
    final private long playerId;

    @JsonCreator
    public PlayerAddedEvent(@JsonProperty("session") long session, @JsonProperty("playerId") long player) {
        this.session = session;
        this.playerId = player;
    }

    @Override
    public long getSession() {
        return session;
    }

    @Override
    public long getPlayerId() {
        return playerId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (playerId ^ (playerId >>> 32));
        result = prime * result + (int) (session ^ (session >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerAddedEvent other = (PlayerAddedEvent) obj;
        if (playerId != other.playerId)
            return false;
        if (session != other.session)
            return false;
        return true;
    }

}