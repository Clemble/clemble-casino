package com.clemble.casino.server.bonus;

import com.clemble.casino.money.Operation;
import com.clemble.casino.payment.*;
import com.clemble.casino.payment.bonus.PaymentBonusSource;
import com.clemble.casino.payment.bonus.PaymentBonusSourceAware;
import com.clemble.casino.payment.event.PaymentBonusEvent;
import com.clemble.casino.money.Money;
import com.clemble.casino.player.PlayerAware;

import java.util.Date;

public class BonusPaymentTransaction implements PlayerAware, AmountAware, PaymentBonusSourceAware, PaymentTransactionConvertible {

    /**
     * Generated 08/01/14
     */
    private static final long serialVersionUID = 1L;

    final private String transactionKey;
    final private String player;
    final private PaymentBonusSource bonusSource;
    final private Money amount;

    public BonusPaymentTransaction(String player, String transactionKey, PaymentBonusSource bonusSource, Money amount) {
        this.bonusSource = bonusSource;
        this.transactionKey = transactionKey;
        this.player = player;
        this.amount = amount;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public Money getAmount() {
        return amount;
    }

    @Override
    public PaymentBonusSource getBonusSource() {
        return bonusSource;
    }

    @Override
    public String getTransactionKey(){
        return transactionKey;
    }

    public PaymentTransaction toTransaction() {
        return new PaymentTransaction().
            setTransactionKey(transactionKey).
            setTransactionDate(new Date()).
                addOperation(new PaymentOperation(PlayerAware.DEFAULT_PLAYER, amount, Operation.Credit)).
                addOperation(new PaymentOperation(player, amount, Operation.Debit));

    }

    public PaymentBonusEvent toEvent(){
        return new PaymentBonusEvent(player, amount, bonusSource, transactionKey);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((bonusSource == null) ? 0 : bonusSource.hashCode());
        result = prime * result + ((player == null) ? 0 : player.hashCode());
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
        BonusPaymentTransaction other = (BonusPaymentTransaction) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (bonusSource != other.bonusSource)
            return false;
        if (player == null) {
            if (other.player != null)
                return false;
        } else if (!player.equals(other.player))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "bonusTransaction:" + player + ":" + bonusSource + ":" + amount;
    }

}
