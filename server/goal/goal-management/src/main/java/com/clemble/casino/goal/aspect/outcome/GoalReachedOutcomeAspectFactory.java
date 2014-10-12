package com.clemble.casino.goal.aspect.outcome;

import com.clemble.casino.goal.aspect.GoalAspectFactory;
import com.clemble.casino.goal.lifecycle.configuration.GoalConfiguration;
import com.clemble.casino.goal.lifecycle.management.GoalState;
import com.clemble.casino.goal.lifecycle.management.event.GoalReachedEvent;
import com.clemble.casino.server.aspect.ClembleAspect;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import org.springframework.core.Ordered;

/**
 * Created by mavarazy on 10/9/14.
 */
public class GoalReachedOutcomeAspectFactory implements GoalAspectFactory<GoalReachedEvent>{

    final private SystemNotificationService systemNotificationService;

    public GoalReachedOutcomeAspectFactory(SystemNotificationService systemNotificationService) {
        this.systemNotificationService = systemNotificationService;
    }

    @Override
    public ClembleAspect<GoalReachedEvent> construct(GoalConfiguration configuration, GoalState context) {
        return new GoalReachedOutcomeAspect(context.getPlayer(), configuration.getBid().total(), systemNotificationService);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

}