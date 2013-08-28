package com.gogomaya.server.game.event.schedule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gogomaya.server.event.GameConstructionEvent;
import com.gogomaya.server.game.construct.GameRequest;

@JsonTypeName("invited")
public class PlayerInvitedEvent implements GameConstructionEvent {

    /**
     * Generated 02/06/2013
     */
    private static final long serialVersionUID = 1753173974867187325L;

    final private long session;

    final private GameRequest gameRequest;

    @JsonCreator
    public PlayerInvitedEvent(@JsonProperty("session") long session, @JsonProperty("gameRequest") GameRequest request) {
        this.session = session;
        this.gameRequest = request;
    }

    @Override
    public long getSession() {
        return session;
    }

    public GameRequest getGameRequest() {
        return gameRequest;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gameRequest == null) ? 0 : gameRequest.hashCode());
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
        PlayerInvitedEvent other = (PlayerInvitedEvent) obj;
        if (gameRequest == null) {
            if (other.gameRequest != null)
                return false;
        } else if (!gameRequest.equals(other.gameRequest))
            return false;
        if (session != other.session)
            return false;
        return true;
    }

}