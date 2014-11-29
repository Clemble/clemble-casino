package com.clemble.casino.goal.aspect.notification;

import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.server.player.notification.ServerNotificationService;

import java.util.Collection;

/**
 * Created by mavarazy on 11/16/14.
 */
public class GoalPlayerNotificationAspect extends GoalAspect<GoalManagementEvent> {

    final private Collection<String> participants;
    final private ServerNotificationService notificationService;

    public GoalPlayerNotificationAspect(Collection<String> participants, ServerNotificationService notificationService){
        super(new EventTypeSelector(GoalManagementEvent.class));
        this.participants = participants;
        this.notificationService = notificationService;
    }

    @Override
    protected void doEvent(GoalManagementEvent event) {
        notificationService.send(participants, event);
    }

}
