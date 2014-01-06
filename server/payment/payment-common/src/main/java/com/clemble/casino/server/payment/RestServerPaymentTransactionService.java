package com.clemble.casino.server.payment;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.clemble.casino.ServerRegistry;
import com.clemble.casino.payment.PaymentTransaction;
import com.clemble.casino.payment.PaymentTransactionKey;
import com.clemble.casino.web.payment.PaymentWebMapping;

public class RestServerPaymentTransactionService implements ServerPaymentTransactionService {

    final private RestTemplate restTemplate;
    final private ServerRegistry paymentServerRegistry;

    public RestServerPaymentTransactionService(ServerRegistry serverRegistryService, RestTemplate restTemplate) {
        this.paymentServerRegistry = checkNotNull(serverRegistryService);
        this.restTemplate = checkNotNull(restTemplate);
    }

    @Override
    public PaymentTransaction process(PaymentTransaction paymentTransaction) {
        // Step 1. Processing transaction key
        PaymentTransactionKey transactionKey = paymentTransaction.getTransactionKey();
        // Step 2. Generating url for payment transaction processing
        String url = paymentServerRegistry.findByIdAndType(transactionKey.getTransaction(), transactionKey.getSource())
                + PaymentWebMapping.PAYMENT_TRANSACTIONS;
        return restTemplate.postForEntity(url, paymentTransaction, PaymentTransaction.class).getBody();
    }

    @Override
    public PaymentTransaction getTransaction(String source, String transactionId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PaymentTransaction> getPlayerTransactions(String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PaymentTransaction> getPlayerTransactionsWithSource(String player, String source) {
        throw new UnsupportedOperationException();
    }

}