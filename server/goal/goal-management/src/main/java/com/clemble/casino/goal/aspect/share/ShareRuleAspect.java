package com.clemble.casino.goal.aspect.share;

import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.goal.aspect.GoalAspect;
import com.clemble.casino.goal.lifecycle.configuration.rule.share.ShareRule;
import com.clemble.casino.goal.lifecycle.management.event.GoalManagementEvent;
import com.clemble.casino.server.event.share.SystemSharePostEvent;
import com.clemble.casino.server.player.notification.SystemNotificationService;

/**
 * Created by mavarazy on 1/10/15.
 */
public class ShareRuleAspect extends GoalAspect<GoalManagementEvent>{

    final private ShareRule shareRule;
    final private SystemNotificationService notificationService;

    public ShareRuleAspect(ShareRule shareRule, SystemNotificationService notificationService) {
        super(new EventTypeSelector(GoalManagementEvent.class));
        this.shareRule = shareRule;
        this.notificationService = notificationService;
    }

    @Override
    protected void doEvent(GoalManagementEvent event) {
        // Step 1. Generating share post event
        SystemSharePostEvent sharePostEvent = new SystemSharePostEvent(event.getPlayer(), shareRule.name(), event.toPost());
        // Step 2. Checking share post event
        notificationService.send(sharePostEvent);
    }

}