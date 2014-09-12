package com.clemble.casino.server.event.game;

import com.clemble.casino.server.event.SystemEvent;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mavarazy on 9/12/14.
 */
public class SystemGameInitiationDueEvent implements SystemGameEvent {

    final public static String CHANNEL = "game:initiation:due";

    final private String sessionKey;

    @JsonCreator
    public SystemGameInitiationDueEvent(@JsonProperty(SESSION_KEY) String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public String getChannel() {
        return CHANNEL;
    }

}