package com.clemble.casino.integration.game;

import java.io.Closeable;

import com.clemble.casino.event.ClientEvent;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.SessionAware;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.event.client.GameClientEvent;
import com.clemble.casino.game.specification.GameSpecificationAware;
import com.clemble.casino.integration.player.Player;

public interface GameSessionPlayer<State extends GameState> extends SessionAware, Closeable, GameSpecificationAware {

    public Player getPlayer();

    public GameConstruction getConstructionInfo();

    public State getState();

    public boolean isAlive();

    public void syncWith(GameSessionPlayer<State> anotherState);

    public void waitForStart();

    public void waitForTurn();

    public int getVersion();

    public void waitVersion(int version);

    public boolean isToMove();

    public ClientEvent getNextMove();

    public void perform(GameClientEvent gameAction);

    public void giveUp();

    public void close();

}