package com.gogomaya.server.integration.player;

import com.gogomaya.server.player.PlayerProfile;
import com.gogomaya.server.player.security.PlayerCredential;
import com.gogomaya.server.player.web.RegistrationRequest;

public interface PlayerOperations {

    public Player login(PlayerCredential credential);

    public Player createPlayer();

    public Player createPlayer(PlayerProfile playerProfile);

    public Player createPlayer(RegistrationRequest registrationRequest);

}