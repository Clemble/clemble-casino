package com.gogomaya.server.game.construct;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.game.specification.GameSpecificationAware;
import com.gogomaya.server.player.PlayerAware;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
abstract public class GameRequest implements PlayerAware, GameSpecificationAware {

    /**
     * Generated 10/06/13
     */
    private static final long serialVersionUID = 4949060894194971610L;

    private long playerId;
    private GameSpecification specification;

    public GameRequest() {
    }

    @Override
    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    @Override
    public GameSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(GameSpecification specification) {
        this.specification = specification;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (playerId ^ (playerId >>> 32));
        result = prime * result + ((specification == null) ? 0 : specification.hashCode());
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
        GameRequest other = (GameRequest) obj;
        if (playerId != other.playerId)
            return false;
        if (specification == null) {
            if (other.specification != null)
                return false;
        } else if (!specification.equals(other.specification))
            return false;
        return true;
    }

}
