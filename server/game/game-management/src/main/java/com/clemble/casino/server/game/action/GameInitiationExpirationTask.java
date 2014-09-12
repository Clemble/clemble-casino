package com.clemble.casino.server.game.action;

import com.clemble.casino.event.Event;
import com.clemble.casino.game.construction.GameInitiation;
import com.clemble.casino.game.construction.event.GameInitiationExpired;
import com.clemble.casino.server.event.game.SystemGameInitiationDueEvent;
import com.clemble.casino.server.executor.EventTask;
import org.springframework.scheduling.TriggerContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * Created by mavarazy on 9/12/14.
 */
public class GameInitiationExpirationTask implements EventTask {

    final private String sessionKey;
    final private Date expirationTime;

    public GameInitiationExpirationTask(String sessionKey, Date expirationTime) {
        this.expirationTime = expirationTime;
        this.sessionKey = sessionKey;
    }

    @Override
    public Collection<SystemGameInitiationDueEvent> execute() {
        return Collections.singleton(new SystemGameInitiationDueEvent(sessionKey));
    }

    @Override
    public String getKey() {
        return sessionKey;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        return expirationTime;
    }
}