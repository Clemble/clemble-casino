package com.clemble.casino.player;

import java.util.ArrayList;
import java.util.Collection;

import com.clemble.casino.event.Event;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.SessionAware;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayerPresence implements Event, PlayerAware, SessionAware, PresenceAware {

    /**
     * Generated
     */
    private static final long serialVersionUID = 2110453101269621164L;

    final private String playerId;
    final private Presence presence;
    final private GameSessionKey session;

    @JsonCreator
    public PlayerPresence(@JsonProperty(PlayerAware.JSON_ID) String player, @JsonProperty("session") GameSessionKey session, @JsonProperty("presence") Presence presence) {
        this.playerId = player;
        this.session = session;
        this.presence = presence;
    }

    @Override
    public String getPlayer() {
        return playerId;
    }

    @Override
    public Presence getPresence() {
        return presence;
    }

    @Override
    public GameSessionKey getSession() {
        return session;
    }

    public static PlayerPresence offline(String player) {
        return new PlayerPresence(player, SessionAware.DEFAULT_SESSION, Presence.offline);
    }

    public static PlayerPresence online(String player) {
        return new PlayerPresence(player, SessionAware.DEFAULT_SESSION, Presence.online);
    }

    public static PlayerPresence playing(String player, GameSessionKey session) {
        return new PlayerPresence(player, session, Presence.playing);
    }

    public static PlayerPresence create(String player, Presence presence) {
        return new PlayerPresence(player, SessionAware.DEFAULT_SESSION, presence);
    }

    public static Collection<PlayerPresence> playing(Collection<String> players, GameSessionKey session) {
        Collection<PlayerPresence> playerPresences = new ArrayList<>();
        for (String player : players)
            playerPresences.add(playing(player, session));
        return playerPresences;
    }

}