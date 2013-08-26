package com.gogomaya.server.integration.game.tictactoe;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import com.gogomaya.server.game.cell.Cell;
import com.gogomaya.server.game.event.client.BetEvent;
import com.gogomaya.server.game.event.client.generic.SelectCellEvent;
import com.gogomaya.server.integration.game.GameSessionPlayer;
import com.gogomaya.server.integration.game.GenericGameSessionPlayer;
import com.gogomaya.server.tictactoe.PicPacPoeState;

public class PicPacPoeSessionPlayer extends GenericGameSessionPlayer<PicPacPoeState> {

    /**
     * Generated 04/07/13
     */
    private static final long serialVersionUID = -2100664121282462477L;

    final private AtomicInteger moneySpent = new AtomicInteger();

    public PicPacPoeSessionPlayer(final GameSessionPlayer<PicPacPoeState> delegate) {
        super(delegate);
    }

    public void select(int row, int column) {
        int beforeSelecting = this.getState() != null ? this.getState().getVersion() : -1;
        // Step 1. Generating bet move
        perform(new SelectCellEvent(actualPlayer.getPlayerId(), Cell.create(row, column)));
        Assert.assertNotSame(beforeSelecting + " remained " + this.getState().getVersion(), this.getState().getVersion());
    }

    public void bet(int amount) {
        int beforeBetting = this.getState().getVersion();
        perform(new BetEvent(actualPlayer.getPlayerId(), amount));
        Assert.assertNotSame(beforeBetting + " remained " + this.getState().getVersion(), beforeBetting, this.getState().getVersion());
        moneySpent.getAndAdd(-amount);
    }
    
    public long getMoneyLeft(){
        return getState().getAccount().getPlayerAccount(getPlayerId()).getMoneyLeft();
    }

    public int getMoneySpent() {
        return moneySpent.get();
    }

}