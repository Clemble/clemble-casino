package com.clemble.casino.goal.aspect.time;

import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.event.Event;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.aspect.ShortGoalAspect;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalEndedEvent;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.lifecycle.management.event.action.PlayerAction;
import com.clemble.casino.player.PlayerAware;
import com.clemble.casino.server.aspect.time.PlayerClockTimeoutEventTask;
import com.clemble.casino.server.event.goal.SystemGoalTimeoutEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mavarazy on 10/9/14.
 */
public class ShortGoalTimeAspect extends ShortGoalAspect<Event> {

    final private Map<String, PlayerClockTimeoutEventTask> playerToTask = new HashMap<>();

    public ShortGoalTimeAspect(
        GoalState state,
        SystemNotificationService systemNotificationService) {
        super(new EventTypeSelector(Event.class));

        state.getContext().getPlayerContexts().forEach(playerContext -> {
            PlayerClockTimeoutEventTask timeoutEventTask = new PlayerClockTimeoutEventTask(
                state.getGoalKey(),
                playerContext.getPlayer(),
                playerContext.getClock(),
                state.getConfiguration().getMoveTimeRule(),
                state.getConfiguration().getTotalTimeRule(),
                systemNotificationService,
                (goalKey) -> new SystemGoalTimeoutEvent(goalKey));
            playerToTask.put(playerContext.getPlayer(), timeoutEventTask);
        });
    }

    @Override
    protected void doEvent(Event move) {
        // Step 1. To check if we need rescheduling, first calculate time before
        if (move instanceof PlayerAction) {
            playerToTask.values().forEach(task -> {
                task.stop();
            });
        } else if(move instanceof GoalEndedEvent) {
            playerToTask.values().forEach(task -> {
                task.stop();
            });
        } else {
            playerToTask.values().forEach(task -> {
                task.start();
            });
        }
    }

}
