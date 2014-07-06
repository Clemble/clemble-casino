package com.clemble.casino.server.event;

import com.clemble.casino.game.GameSessionAware;
import com.clemble.casino.game.GameSessionKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Created by mavarazy on 7/4/14.
 */
public class SystemGameEndedEvent implements SystemEvent, GameSessionAware {

    final public static String CHANNEL = "game:ended";

    final private GameSessionKey sessionKey;
    final private Collection<String> participants;

    @JsonCreator
    public SystemGameEndedEvent(@JsonProperty("session") GameSessionKey sessionKey, @JsonProperty("participants") Collection<String> participants) {
        this.sessionKey = sessionKey;
        this.participants = participants;
    }

    @Override
    public GameSessionKey getSession() {
        return sessionKey;
    }

    public Collection<String> getParticipants() {
        return participants;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }
}