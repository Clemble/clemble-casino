package com.gogomaya.server.integration;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gogomaya.base.ActionLatch;
import com.gogomaya.error.GogomayaError;
import com.gogomaya.error.GogomayaException;
import com.gogomaya.event.ClientEvent;
import com.gogomaya.game.GameState;
import com.gogomaya.game.account.GameAccount;
import com.gogomaya.game.event.client.surrender.SurrenderEvent;
import com.gogomaya.game.event.server.GameEndedEvent;
import com.gogomaya.game.event.server.GameServerEvent;
import com.gogomaya.game.iterator.GamePlayerIterator;
import com.gogomaya.game.outcome.DrawOutcome;
import com.gogomaya.game.outcome.GameOutcome;
import com.gogomaya.game.outcome.NoOutcome;
import com.gogomaya.game.outcome.PlayerWonOutcome;

@JsonTypeName("number")
public class NumberState implements GameState {

    /**
     * 
     */
    private static final long serialVersionUID = 6467228372170341563L;

    final private ActionLatch actionLatch;
    final private GameAccount gameAccount;
    final private GamePlayerIterator playerIterator;

    private GameOutcome outcome;
    private int version;

    public NumberState(ActionLatch expectedActions, GameAccount gameAccount, GamePlayerIterator playerIterator) {
        this(expectedActions, gameAccount, playerIterator, null, 0);
    }

    @JsonCreator
    public NumberState(@JsonProperty("actionLatch") ActionLatch expectedActions,
            @JsonProperty("account") GameAccount gameAccount,
            @JsonProperty("playerIterator") GamePlayerIterator playerIterator,
            @JsonProperty("outcome") GameOutcome outcome,
            @JsonProperty("version") int version) {
        this.actionLatch = expectedActions;
        this.gameAccount = gameAccount;
        this.playerIterator = playerIterator;
        this.outcome = outcome;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GameServerEvent<NumberState> process(ClientEvent clientEvent) {
        // Step 1. Processing Select cell move
        if (outcome != null) {
            throw GogomayaException.fromError(GogomayaError.GamePlayGameEnded);
        }

        GameServerEvent<NumberState> resultEvent = null;

        if (clientEvent instanceof SelectNumberEvent) {
            actionLatch.put(clientEvent.getPlayerId(), clientEvent);
            if (actionLatch.complete()) {
                int maxBet = 0;
                for (ClientEvent madeMove : actionLatch.fetchActionsMap().values()) {
                    SelectNumberEvent selectNumberEvent = (SelectNumberEvent) madeMove;
                    if (selectNumberEvent.getNumber() > maxBet) {
                        maxBet = selectNumberEvent.getNumber();
                        outcome = new PlayerWonOutcome(selectNumberEvent.getPlayerId());
                    } else if (selectNumberEvent.getNumber() == maxBet) {
                        outcome = new DrawOutcome();
                    }
                }
            }
            resultEvent = new GameEndedEvent<NumberState>(this).setOutcome(outcome);
        } else if (clientEvent instanceof SurrenderEvent) {
            // Step 1. Fetching player identifier
            long looser = ((SurrenderEvent) clientEvent).getPlayerId();
            Collection<Long> opponents = playerIterator.whoIsOpponents(looser);
            if (opponents.size() == 0 || version == 1) {
                // Step 2. No game started just live the table
                outcome = new NoOutcome();
                resultEvent = new GameEndedEvent<NumberState>(this).setOutcome(outcome);
            } else {
                long winner = opponents.iterator().next();
                outcome = new PlayerWonOutcome(winner);
                // Step 2. Player gave up, consists of 2 parts - Gave up, and Ended since there is no players involved
                resultEvent = new GameEndedEvent<NumberState>(this).setOutcome(outcome);
            }
        }
        // Step 3. Sanity check
        if (resultEvent == null)
            throw GogomayaException.fromError(GogomayaError.GamePlayMoveNotSupported);
        // Step 4. Updating version and returning event
        version++;
        return resultEvent;
    }

    @Override
    public GameAccount getAccount() {
        return gameAccount;
    }

    @Override
    public GamePlayerIterator getPlayerIterator() {
        return playerIterator;
    }

    @Override
    public ActionLatch getActionLatch() {
        return actionLatch;
    }

    @Override
    public GameOutcome getOutcome() {
        return outcome;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actionLatch == null) ? 0 : actionLatch.hashCode());
        result = prime * result + ((gameAccount == null) ? 0 : gameAccount.hashCode());
        result = prime * result + ((outcome == null) ? 0 : outcome.hashCode());
        result = prime * result + ((playerIterator == null) ? 0 : playerIterator.hashCode());
        result = prime * result + version;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NumberState other = (NumberState) obj;
        if (actionLatch == null) {
            if (other.actionLatch != null)
                return false;
        } else if (!actionLatch.equals(other.actionLatch))
            return false;
        if (gameAccount == null) {
            if (other.gameAccount != null)
                return false;
        } else if (!gameAccount.equals(other.gameAccount))
            return false;
        if (outcome == null) {
            if (other.outcome != null)
                return false;
        } else if (!outcome.equals(other.outcome))
            return false;
        if (playerIterator == null) {
            if (other.playerIterator != null)
                return false;
        } else if (!playerIterator.equals(other.playerIterator))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

}