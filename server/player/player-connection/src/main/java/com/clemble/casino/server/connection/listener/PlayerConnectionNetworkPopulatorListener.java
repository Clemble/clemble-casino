package com.clemble.casino.server.connection.listener;

import com.clemble.casino.server.event.player.SystemPlayerConnectionsFetchedEvent;
import com.clemble.casino.server.event.player.SystemPlayerDiscoveredConnectionEvent;
import com.clemble.casino.server.player.notification.SystemEventListener;
import com.clemble.casino.server.player.notification.SystemNotificationService;
import com.clemble.casino.server.connection.PlayerConnectionNetwork;
import com.clemble.casino.server.connection.repository.PlayerConnectionNetworkRepository;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

/**
 * Created by mavarazy on 7/4/14.
 */
public class PlayerConnectionNetworkPopulatorListener implements SystemEventListener<SystemPlayerConnectionsFetchedEvent> {

    final private PlayerConnectionNetworkRepository socialNetworkRepository;
    final private SystemNotificationService notificationService;

    public PlayerConnectionNetworkPopulatorListener(
        PlayerConnectionNetworkRepository socialNetworkRepository,
        SystemNotificationService notificationService) {
        this.socialNetworkRepository = checkNotNull(socialNetworkRepository);
        this.notificationService = checkNotNull(notificationService);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void onEvent(SystemPlayerConnectionsFetchedEvent event) {
        // Step 1. Finding appropriate SocialConnectionAdapter
        Collection<PlayerConnectionNetwork> connectionsBefore = ImmutableList.copyOf(socialNetworkRepository.findRelations(event.getPlayer()));
        //TODO Switch to Neo4jTemplate, otherwise this kills saved relationships
        PlayerConnectionNetwork socialNetwork = new PlayerConnectionNetwork(event.getPlayer());
        socialNetwork.addOwned(event.getConnection());
        // Step 4. Updating socialNetwork with new connections
        socialNetwork.addConnections(event.getConnections());
        // Step 5. Saving social network repository
        socialNetworkRepository.save(socialNetwork);
        // Step 6. Fetching new connections
        Collection<PlayerConnectionNetwork> connectionsAfter = new ArrayList<>(ImmutableList.copyOf(socialNetworkRepository.findRelations(event.getPlayer())));
        // Step 7. Finding difference
        connectionsAfter.removeAll(connectionsBefore);
        for (PlayerConnectionNetwork newConnection : connectionsAfter)
            notificationService.notify(new SystemPlayerDiscoveredConnectionEvent(event.getPlayer(), newConnection.getPlayer()));
    }

    @Override
    public String getChannel() {
        return SystemPlayerConnectionsFetchedEvent.CHANNEL;
    }

    @Override
    public String getQueueName() {
        return SystemPlayerConnectionsFetchedEvent.CHANNEL + " > player:connection:populator";
    }

}