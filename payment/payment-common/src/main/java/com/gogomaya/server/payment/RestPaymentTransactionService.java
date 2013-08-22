package com.gogomaya.server.payment;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.web.client.RestTemplate;

import com.gogomaya.server.payment.web.mapping.PaymentWebMapping;

public class RestPaymentTransactionService implements PaymentTransactionService {

    final private String baseUrl;
    final private RestTemplate restTemplate;
    
    public RestPaymentTransactionService(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = checkNotNull(baseUrl);
        this.restTemplate = checkNotNull(restTemplate);
    }

    @Override
    public PaymentTransaction process(PaymentTransaction paymentTransaction) {
        return restTemplate.postForEntity(baseUrl + PaymentWebMapping.ACCOUNT_PREFIX + PaymentWebMapping.PAYMENT_TRANSACTIONS, paymentTransaction,
                PaymentTransaction.class).getBody();
    }

}