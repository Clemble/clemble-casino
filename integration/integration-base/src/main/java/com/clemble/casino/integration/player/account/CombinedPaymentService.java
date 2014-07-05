package com.clemble.casino.integration.player.account;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.PlayerAccount;
import com.clemble.casino.payment.money.Currency;
import com.clemble.casino.payment.service.PaymentService;
import com.clemble.casino.payment.service.PaymentTransactionService;
import com.clemble.casino.payment.service.PlayerAccountService;
import org.springframework.web.bind.annotation.RequestParam;

public class CombinedPaymentService implements PaymentService {

    final private PaymentTransactionService paymentTransactionService;
    final private PlayerAccountService playerAccountService;

    public CombinedPaymentService(PaymentTransactionService transactionService, PlayerAccountService accountService) {
        this.playerAccountService = checkNotNull(accountService);
        this.paymentTransactionService = checkNotNull(transactionService);
    }

    @Override
    public PlayerAccount get(String playerId) {
        return playerAccountService.get(playerId);
    }

    @Override
    public List<String> canAfford(Collection<String> players, Currency currency, Long amount) {
        return playerAccountService.canAfford(players, currency, amount);
    }

    @Override
    public PaymentTransaction getTransaction(String source, String transactionId) {
        return paymentTransactionService.getTransaction(source, transactionId);
    }

    @Override
    public List<PaymentTransaction> getPlayerTransactions(String player) {
        return paymentTransactionService.getPlayerTransactions(player);
    }

    @Override
    public List<PaymentTransaction> getPlayerTransactionsWithSource(String player, String source) {
        return paymentTransactionService.getPlayerTransactionsWithSource(player, source);
    }

}
