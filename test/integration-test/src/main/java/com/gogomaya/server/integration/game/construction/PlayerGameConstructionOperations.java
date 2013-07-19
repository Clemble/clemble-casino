package com.gogomaya.server.integration.game.construction;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;

import com.gogomaya.server.game.GameAware;
import com.gogomaya.server.game.GameState;
import com.gogomaya.server.game.construct.GameRequest;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.integration.game.GameSessionPlayer;
import com.gogomaya.server.integration.player.Player;

public class PlayerGameConstructionOperations<State extends GameState> implements GameAware {

    /**
     * Generated 03/07/13
     */
    private static final long serialVersionUID = 1645707988649743797L;

    final private GameConstructionOperations<State> gameOperations;
    final private Player player;

    public PlayerGameConstructionOperations(GameConstructionOperations<State> gameOperations, Player player) {
        this.gameOperations = checkNotNull(gameOperations);
        this.player = player;
    }

    @Override
    public String getName() {
        return gameOperations.getName();
    }

    public GameSpecification selectSpecification() {
        return gameOperations.selectSpecification(player);
    }

    public GameSessionPlayer<State> constructAvailability(Player... participants) {
        Collection<Long> participantIds = new ArrayList<>();
        for (Player player : participants)
            participantIds.add(player.getPlayerId());
        return constructAvailability(participantIds);
    }

    public GameSessionPlayer<State> constructAvailability(Collection<Long> participants) {
        return gameOperations.constructAvailability(player, selectSpecification(), participants);
    }

    public GameSessionPlayer<State> constructAvailability(GameSpecification specification, Collection<Long> participants) {
        return gameOperations.constructAvailability(player, specification, participants);
    }

    public GameSessionPlayer<State> construct(GameRequest request) {
        return gameOperations.construct(player, request);
    }

    public GameSessionPlayer<State> acceptInvitation(long construction) {
        return gameOperations.acceptInvitation(player, construction);
    }

    public void declineInvitation(long construction) {
        gameOperations.declineInvitation(player, construction);
    }

}