package com.clemble.casino.server.player.account;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.clemble.casino.money.Currency;
import com.clemble.casino.money.Money;
import com.clemble.casino.money.MoneySource;
import com.clemble.casino.money.Operation;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.PaymentTransactionKey;
import com.clemble.casino.payment.PlayerAccount;
import com.clemble.casino.player.PlayerAware;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.server.payment.PaymentTransactionServerService;
import com.clemble.casino.server.player.account.PlayerAccountServerService;
import com.clemble.casino.server.repository.player.PlayerAccountRepository;

public class PlayerAccountServerServiceImpl implements PlayerAccountServerService {

    final private PlayerAccountRepository playerAccountRepository;
    final private PaymentTransactionServerService paymentTransactionService;

    public PlayerAccountServerServiceImpl(final PlayerAccountRepository playerWalletRepository, final PaymentTransactionServerService paymentTransactionService) {
        this.playerAccountRepository = checkNotNull(playerWalletRepository);
        this.paymentTransactionService = checkNotNull(paymentTransactionService);
    }

    @Override
    public PlayerAccount register(PlayerProfile player) {
        // Step 1. Creating initial empty account
        PlayerAccount initialWallet = new PlayerAccount().setPlayer(player.getPlayer());
        initialWallet = playerAccountRepository.save(initialWallet);
        // Step 2. Creating initial empty
        Money initialBalance = Money.create(Currency.FakeMoney, 500);
        PaymentTransaction initialTransaction = new PaymentTransaction()
                .setTransactionKey(new PaymentTransactionKey(MoneySource.Registration, player.getPlayer()))
                .addPaymentOperation(new PaymentOperation().setOperation(Operation.Debit).setAmount(initialBalance).setPlayer(player.getPlayer()))
                .addPaymentOperation(new PaymentOperation().setOperation(Operation.Credit).setAmount(initialBalance).setPlayer(PlayerAware.DEFAULT_PLAYER));
        // Step 3. Returning PaymentTransaction
        paymentTransactionService.process(initialTransaction);
        return playerAccountRepository.findOne(player.getPlayer());
    }

    @Override
    public boolean canAfford(String player, Money amount) {
        // Step 1. Retrieving players account
        PlayerAccount playerAccount = playerAccountRepository.findOne(player);
        Money existingAmmount = playerAccount.getMoney(amount.getCurrency());
        // Step 2. If existing amount is not enough player can't afford it
        return existingAmmount.getAmount() >= amount.getAmount();
    }

    @Override
    public boolean canAfford(Collection<String> players, Money amount) {
        for (String player : players) {
            if (!canAfford(player, amount))
                return false;
        }
        return true;
    }

}