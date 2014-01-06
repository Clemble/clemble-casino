package com.clemble.casino.server.player.registration;

import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.player.SocialAccessGrant;
import com.clemble.casino.player.SocialConnectionData;
import com.clemble.casino.web.player.PlayerWebMapping;
import org.springframework.web.client.RestTemplate;

import static com.google.common.base.Preconditions.checkNotNull;

public class RestServerProfileRegistrationService implements ServerProfileRegistrationService {

    final private String baseUrl;
    final private RestTemplate restTemplate;

    public RestServerProfileRegistrationService(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = checkNotNull(baseUrl);
        this.restTemplate = checkNotNull(restTemplate);
    }

    @Override
    public PlayerProfile create(PlayerProfile playerProfile) {
        return restTemplate.postForEntity(baseUrl + PlayerWebMapping.PLAYER_PROFILE_REGISTRATION, playerProfile, PlayerProfile.class).getBody();
    }

    @Override
    public PlayerProfile create(SocialConnectionData socialConnectionData) {
        return restTemplate.postForEntity(baseUrl + PlayerWebMapping.PLAYER_PROFILE_REGISTRATION_SOCIAL, socialConnectionData, PlayerProfile.class).getBody();
    }

    @Override
    public PlayerProfile create(SocialAccessGrant accessGrant) {
        return restTemplate.postForEntity(baseUrl + PlayerWebMapping.PLAYER_PROFILE_REGISTRATION_GRANT, accessGrant, PlayerProfile.class).getBody();
    }

}