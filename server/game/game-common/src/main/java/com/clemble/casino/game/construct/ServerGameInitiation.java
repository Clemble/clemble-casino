package com.clemble.casino.game.construct;

import java.util.List;

import com.clemble.casino.game.RoundGameContext;
import com.clemble.casino.game.GameSessionAware;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.specification.GameConfigurationAware;
import com.clemble.casino.game.specification.RoundGameConfiguration;
import com.clemble.casino.player.PlayerAwareUtils;

public class ServerGameInitiation implements GameSessionAware, GameConfigurationAware {

    /**
     * Generated 23/01/14
     */
    private static final long serialVersionUID = -3413033971589908905L;

    final private RoundGameContext context;
    final private GameSessionKey sessionKey;
    final private RoundGameConfiguration configuration;
    final private List<String> participants;

    public ServerGameInitiation(GameSessionKey sessionKey, RoundGameContext context, RoundGameConfiguration specification) {
        this.context = context;
        this.sessionKey = sessionKey;
        this.configuration = specification;
        this.participants = PlayerAwareUtils.toPlayerList(context.getPlayerContexts());
    }

    @Override
    public GameSessionKey getSession() {
        return sessionKey;
    }

    @Override
    public RoundGameConfiguration getConfiguration() {
        return configuration;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public RoundGameContext getContext() {
        return context;
    }

}