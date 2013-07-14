package com.gogomaya.server.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.gogomaya.server.integration.game.construction.GameConstructionOperations;
import com.gogomaya.server.integration.player.listener.PlayerListenerOperations;
import com.gogomaya.server.integration.player.profile.PlayerProfileOperations;
import com.gogomaya.server.player.security.PlayerCredential;
import com.gogomaya.server.player.security.PlayerIdentity;
import com.gogomaya.server.player.security.PlayerSession;
import com.gogomaya.server.player.wallet.PlayerWallet;
import com.gogomaya.server.player.web.RegistrationRequest;
import com.gogomaya.server.web.player.registration.RegistrationLoginController;
import com.gogomaya.server.web.player.registration.RegistrationSignInContoller;
import com.gogomaya.server.web.player.session.PlayerSessionController;
import com.gogomaya.server.web.player.wallet.WalletController;

public class WebPlayerOperations extends AbstractPlayerOperations {

    final private RegistrationSignInContoller signInContoller;
    final private RegistrationLoginController loginController;
    final private PlayerSessionController sessionController;
    final private WalletController walletController;

    public WebPlayerOperations(RegistrationSignInContoller signInContoller,
            RegistrationLoginController loginController,
            WalletController walletController,
            PlayerSessionController sessionController,
            PlayerListenerOperations listenerOperations,
            PlayerProfileOperations playerProfileOperations,
            GameConstructionOperations<?>... gameConstructionOperations) {
        super(listenerOperations, playerProfileOperations, gameConstructionOperations);
        this.signInContoller = checkNotNull(signInContoller);
        this.loginController = checkNotNull(loginController);
        this.walletController = checkNotNull(walletController);
        this.sessionController = checkNotNull(sessionController);
    }

    @Override
    public Player createPlayer(RegistrationRequest registrationRequest) {
        // Step 0. Sanity check
        checkNotNull(registrationRequest);
        // Step 1. Performing actual player creation
        PlayerIdentity playerIdentity = signInContoller.createUser(registrationRequest);
        checkNotNull(playerIdentity);
        // Step 2. Generating Player from created request
        Player player = super.create(playerIdentity, registrationRequest.getPlayerCredential());
        // Step 3. Returning player session result
        return player;

    }

    @Override
    public Player login(PlayerCredential credential) {
        // Step 0. Sanity check
        checkNotNull(credential);
        // Step 1. Performing actual player login
        PlayerIdentity playerIdentity = loginController.createUser(credential);
        checkNotNull(playerIdentity);
        // Step 2. Generating Player from credentials
        Player player = super.create(playerIdentity, credential);
        // Step 3. Returning player session result
        return player;
    }

    @Override
    public PlayerWallet wallet(Player player, long playerWalletId) {
        return walletController.get(player.getPlayerId(), playerWalletId);
    }

    public PlayerSession startSession(PlayerIdentity player) {
        return sessionController.startSession(player.getPlayerId());
    }

}
