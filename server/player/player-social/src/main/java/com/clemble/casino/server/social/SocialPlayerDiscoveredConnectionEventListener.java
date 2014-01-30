package com.clemble.casino.server.social;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import com.clemble.casino.player.PlayerDiscoveredConnectionEvent;
import com.clemble.casino.server.event.SystemPlayerDiscoveredConnectionEvent;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.clemble.casino.server.player.notification.SystemEventListener;

public class SocialPlayerDiscoveredConnectionEventListener implements SystemEventListener<SystemPlayerDiscoveredConnectionEvent>{

    final PlayerNotificationService notificationService;

    public SocialPlayerDiscoveredConnectionEventListener(PlayerNotificationService notificationService) {
        this.notificationService = checkNotNull(notificationService);
    }

    @Override
    public void onEvent(SystemPlayerDiscoveredConnectionEvent event) {
        // Step 1. Notifying original
        notificationService.notify(new PlayerDiscoveredConnectionEvent(event.getPlayer(), event.getDiscovered()));
        // Step 1. Notifying discovered player
        notificationService.notify(new PlayerDiscoveredConnectionEvent(event.getDiscovered(), event.getPlayer()));
    }

    @Override
    public String getChannel() {
        return SystemPlayerDiscoveredConnectionEvent.CHANNEL;
    }

    @Override
    public String getQueueName() {
        return "player.social.autodiscovery";
    }

}
