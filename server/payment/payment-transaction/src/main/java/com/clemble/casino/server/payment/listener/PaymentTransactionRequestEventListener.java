package com.clemble.casino.server.payment.listener;

import com.clemble.casino.error.ClembleCasinoError;
import com.clemble.casino.error.ClembleCasinoException;
import com.clemble.casino.payment.PaymentOperation;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.event.FinishedPaymentEvent;
import com.clemble.casino.payment.event.PaymentEvent;
import com.clemble.casino.payment.money.Operation;
import com.clemble.casino.payment.service.PaymentTransactionService;
import com.clemble.casino.server.SystemPaymentTransactionRequestEvent;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.clemble.casino.server.player.notification.SystemEventListener;
import com.clemble.casino.server.repository.payment.PaymentTransactionRepository;
import com.clemble.casino.server.repository.payment.PlayerAccountTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by mavarazy on 7/5/14.
 */
public class PaymentTransactionRequestEventListener implements SystemEventListener<SystemPaymentTransactionRequestEvent>{

    final private PlayerAccountTemplate accountTemplate;
    final private PlayerNotificationService notificationService;
    final private PaymentTransactionRepository paymentTransactionRepository;

    public PaymentTransactionRequestEventListener(
            PaymentTransactionRepository paymentTransactionRepository,
            PlayerAccountTemplate accountTemplate,
            PlayerNotificationService notificationService) {
        this.paymentTransactionRepository = checkNotNull(paymentTransactionRepository);
        this.accountTemplate = checkNotNull(accountTemplate);
        this.notificationService = checkNotNull(notificationService);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEvent(SystemPaymentTransactionRequestEvent event) {
        PaymentTransaction paymentTransaction = event.getTransaction();
        // Step 1. Sanity check
        if (paymentTransaction == null)
            throw ClembleCasinoException.fromError(ClembleCasinoError.PaymentTransactionEmpty);
        // Step 2. Processing payment transactions
        processTransaction(paymentTransaction);
    }

    private PaymentTransaction processTransaction(PaymentTransaction paymentTransaction) {
        // Step 0. Sanity check
        if(paymentTransactionRepository.exists(paymentTransaction.getTransactionKey())) {
            // TODO maybe you need to throw exception here
            return paymentTransactionRepository.findOne(paymentTransaction.getTransactionKey());
        }
        Collection<PaymentEvent> paymentEvents = new ArrayList<>();
        // Step 1. Processing payment transactions
        for (PaymentOperation paymentOperation : paymentTransaction.getPaymentOperations()) {
            if (paymentOperation.getOperation() == Operation.Credit) {
                accountTemplate.credit(paymentOperation.getPlayer(), paymentOperation.getAmount());
            } else if (paymentOperation.getOperation() == Operation.Debit) {
                accountTemplate.debit(paymentOperation.getPlayer(), paymentOperation.getAmount());
            } else {
                throw ClembleCasinoException.fromError(ClembleCasinoError.PaymentTransactionInvalid);
            }
            paymentEvents.add(new FinishedPaymentEvent(paymentTransaction.getTransactionKey(), paymentOperation));
        }
        // Step 3. Saving account transaction
        PaymentTransaction transaction = paymentTransactionRepository.save(paymentTransaction);
        // Step 4. Sending PaymentEvent notification
        notificationService.notify(paymentEvents);
        return transaction;
    }

    @Override
    public String getChannel() {
        return SystemPaymentTransactionRequestEvent.CHANNEL;
    }

    @Override
    public String getQueueName() {
        return "payment:transaction:processor";
    }
}