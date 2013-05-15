package com.gogomaya.server.game.action;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gogomaya.server.player.PlayerAware;

public class SequentialPlayerIterator implements GamePlayerIterator {

    /**
     * Generated 12/04/13
     */
    private static final long serialVersionUID = -4182637038671660855L;

    final private long[] players;

    private int index;

    public SequentialPlayerIterator(Collection<? extends PlayerAware> playerAwares) {
        this.index = 0;
        this.players = new long[playerAwares.size()];
        // Parsing player aware values
        for (PlayerAware playerAware : playerAwares) {
            players[index++] = playerAware.getPlayerId();
        }
    }

    @JsonCreator
    public SequentialPlayerIterator(@JsonProperty("index") final int current, @JsonProperty("players") long[] players) {
        this.players = players;
        this.index = current;
    }

    public SequentialPlayerIterator(final int currentUser, Collection<? extends PlayerAware> playerAwares) {
        this.index = currentUser;
        this.players = new long[playerAwares.size()];

        int i = 0;
        for (PlayerAware playerAware : playerAwares)
            getPlayers()[i++] = playerAware.getPlayerId();
    }

    @Override
    public long next() {
        return getPlayers()[++index % getPlayers().length];
    }

    @Override
    public long current() {
        return getPlayers()[index % getPlayers().length];
    }

    @Override
    public long[] getPlayers() {
        return players;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean contains(long targetPlayerId) {
        for (long playerId : players) {
            if (playerId == targetPlayerId)
                return true;
        }
        return false;
    }

    @Override
    public SequentialPlayerIterator remove(long playerId) {
        long[] playersCopy = new long[players.length - 1];
        int position = 0;
        for(int i = 0, j = 0; i < players.length; i++) {
            if(players[i] != playerId) {
                playersCopy[j++] = players[i];
            } else {
                position = i;
            }
        }
        return new SequentialPlayerIterator(position >= index ? index % players.length : (index + players.length - 1) % players.length, playersCopy);
    }

}
