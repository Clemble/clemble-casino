package com.clemble.casino.integration.game;

import com.clemble.casino.game.construction.AvailabilityGameRequest;
import com.clemble.casino.game.construction.GameConstruction;
import com.clemble.casino.game.construction.event.GameInvitationResponseEvent;
import com.clemble.casino.game.construction.service.AvailabilityGameConstructionService;
import com.clemble.casino.player.event.PlayerEvent;
import com.clemble.casino.server.game.construction.controller.AvailabilityGameConstructionController;

import java.util.Collection;

/**
 * Created by mavarazy on 9/15/14.
 */
public class IntegrationAvailabilityGameConstructionService implements AvailabilityGameConstructionService{

    final private String player;
    final private AvailabilityGameConstructionController constructionController;

    public IntegrationAvailabilityGameConstructionService(String player, AvailabilityGameConstructionController constructionController) {
        this.player = player;
        this.constructionController = constructionController;
    }

    @Override
    public GameConstruction construct(AvailabilityGameRequest gameRequest) {
        return constructionController.construct(player, gameRequest);
    }

    @Override
    public Collection<GameConstruction> getPending(String player) {
        return constructionController.getPending(player);
    }

    @Override
    public GameConstruction getConstruction(String sessionKey) {
        return constructionController.getConstruction(sessionKey);
    }

    @Override
    public PlayerEvent getReply(String sessionKey, String player) {
        return constructionController.getReply(sessionKey, player);
    }

    @Override
    public GameConstruction reply(GameInvitationResponseEvent gameRequest) {
        return constructionController.reply(gameRequest);
    }
}
