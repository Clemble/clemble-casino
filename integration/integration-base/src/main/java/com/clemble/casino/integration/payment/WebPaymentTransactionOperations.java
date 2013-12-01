package com.clemble.casino.integration.payment;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.server.payment.PaymentTransactionServerService;

public class WebPaymentTransactionOperations  extends AbstractPaymentTransactionOperations {

    final private PaymentTransactionServerService paymentTransactionController;

    public WebPaymentTransactionOperations(PaymentTransactionServerService paymentTransactionController) {
        this.paymentTransactionController = checkNotNull(paymentTransactionController);
    }

    @Override
    public PaymentTransaction perform(PaymentTransaction transaction) {
        return paymentTransactionController.process(transaction);
    }

    @Override
    public PaymentTransaction get(ClembleCasinoOperations player, String source, String transactionId) {
        return paymentTransactionController.getPaymentTransaction(source, transactionId);
    }

}
