package com.clemble.casino.server.player.account;

import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.clemble.casino.ServerRegistry;
import com.clemble.casino.payment.PlayerAccount;
import com.clemble.casino.payment.money.Money;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.web.payment.PaymentWebMapping;

public class RestPlayerAccountServerService implements PlayerAccountServerService {

    final private RestTemplate restTemplate;
    final private ServerRegistry paymentServerRegistry;

    public RestPlayerAccountServerService(ServerRegistry paymentServerRegistry, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.paymentServerRegistry = paymentServerRegistry;
    }

    @Override
    public PlayerAccount register(PlayerProfile playerProfile) {
        HttpEntity<PlayerProfile> request = sign(playerProfile); 
        return restTemplate.postForEntity(paymentServerRegistry.findBase() + PaymentWebMapping.PAYMENT_ACCOUNTS,
                request, PlayerAccount.class).getBody();
    }

    @Override
    public boolean canAfford(String playerId, Money amount) {
        return canAfford(Collections.singleton(playerId), amount);
    }

    @Override
    public boolean canAfford(Collection<String> playerId, Money amount) {
        String url = paymentServerRegistry.findBase()
                + PaymentWebMapping.PAYMENT_ACCOUNTS
                + "?player=" + StringUtils.collectionToCommaDelimitedString(playerId)
                + "&currency=" + amount.getCurrency()
                + "&amount=" + amount.getAmount();
        return restTemplate.getForEntity(url, Boolean.class).getBody();
    }

    private <T> HttpEntity<T> sign(T value) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(1);
        headers.add("Content-Type", "application/json");
        HttpEntity<T> request = new HttpEntity<T>(value, headers);
        return request;
    }

}
