package com.clemble.casino.game.service;

import com.clemble.casino.event.ClientEvent;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.construct.GameRequest;
import com.clemble.casino.game.event.schedule.InvitationResponseEvent;

public interface GameConstructionService {

    public GameConstruction construct(final String player, final GameRequest gameRequest);

    public GameConstruction getConstruct(final String player, final Game game, final String session);

    public ClientEvent getResponce(final String requester, final Game game, final String session, final String player);

    public GameConstruction reply(final String player, final Game game, String sessionId, final InvitationResponseEvent gameRequest);

}