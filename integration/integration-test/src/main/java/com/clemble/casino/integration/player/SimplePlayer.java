package com.clemble.casino.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.clemble.casino.client.payment.PaymentTransactionOperations;
import com.clemble.casino.client.payment.PaymentTransactionTemplate;
import com.clemble.casino.client.player.PlayerProfileOperations;
import com.clemble.casino.client.player.PlayerProfileTemplate;
import com.clemble.casino.client.player.PlayerSessionOperations;
import com.clemble.casino.client.player.PlayerSessionTemplate;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.integration.game.GameSessionListener;
import com.clemble.casino.integration.game.construction.GameConstructionOperations;
import com.clemble.casino.integration.game.construction.PlayerGameConstructionOperations;
import com.clemble.casino.integration.player.account.PaymentServiceFactory;
import com.clemble.casino.integration.player.listener.PlayerListenerManager;
import com.clemble.casino.integration.player.listener.PlayerListenerOperations;
import com.clemble.casino.integration.player.profile.PlayerProfileServiceFactory;
import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.player.security.PlayerCredential;
import com.clemble.casino.player.security.PlayerSession;
import com.clemble.casino.player.security.PlayerToken;
import com.clemble.casino.player.service.PlayerSessionService;
import com.google.common.collect.ImmutableMap;

public class SimplePlayer implements Player {

    /**
     * Generated
     */
    private static final long serialVersionUID = -4160641502466429770L;

    final private String player;
    final private PlayerSession session;
    final private PlayerToken identity;
    final private PlayerListenerManager playerListenersManager;
    final private PlayerCredential credential;

    final private Map<Game, PlayerGameConstructionOperations<?>> gameConstructors;

    final private PlayerSessionOperations playerSessionOperations;
    final private PaymentTransactionOperations playerAccountOperations;
    final private PlayerProfileOperations profileOperations;

    public SimplePlayer(final PlayerToken playerIdentity, 
            final PlayerCredential credential,
            final PlayerProfileServiceFactory playerProfileServiceFactory,
            final PlayerSessionService sessionOperations,
            final PaymentServiceFactory accountOperations,
            final PlayerListenerOperations listenerOperations,
            final Collection<GameConstructionOperations<?>> playerConstructionOperations) {
        this.identity = checkNotNull(playerIdentity);
        this.player = playerIdentity.getPlayer();

        this.profileOperations = new PlayerProfileTemplate(player, playerProfileServiceFactory.construct(this));
        this.playerAccountOperations = new PaymentTransactionTemplate(player, accountOperations.construct(this));

        this.playerSessionOperations = new PlayerSessionTemplate(player, sessionOperations);
        this.session = checkNotNull(playerSessionOperations.create());
        this.credential = checkNotNull(credential);
        this.playerListenersManager = new PlayerListenerManager(this, listenerOperations);

        Map<Game, PlayerGameConstructionOperations<?>> map = new HashMap<>();
        for (GameConstructionOperations<?> constructionOperation : playerConstructionOperations) {
            map.put(constructionOperation.getGame(), new PlayerGameConstructionOperations<>(constructionOperation, this));
        }
        this.gameConstructors = ImmutableMap.<Game, PlayerGameConstructionOperations<?>> copyOf(map);
    }

    @Override
    public PlayerSessionOperations getSessionOperations() {
        return playerSessionOperations;
    }

    @Override
    public PlayerProfileOperations getProfileOperations() {
        return profileOperations;
    }

    public PaymentTransactionOperations getWalletOperations() {
        return playerAccountOperations;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @SuppressWarnings("unchecked")
    public <T extends PlayerProfile> T getProfile() {
        return (T) profileOperations.getPlayerProfile();
    }

    public Player setProfile(PlayerProfile newProfile) {
        profileOperations.updatePlayerProfile(newProfile);
        return this;
    }

    public PlayerToken getIdentity() {
        return identity;
    }

    public PlayerCredential getCredential() {
        return credential;
    }

    public PlayerSession getSession() {
        return session;
    }

    @SuppressWarnings("unchecked")
    public <State extends GameState> PlayerGameConstructionOperations<State> getGameConstructor(Game game) {
        return (PlayerGameConstructionOperations<State>) gameConstructors.get(game);
    }

    public Map<Game, PlayerGameConstructionOperations<?>> getGameConstructors() {
        return gameConstructors;
    }

    // TODO When signing key must change, in order to provide safety, otherwise
    // Attacker can emulate user, by sending signed request back
    public <T> HttpEntity<T> sign(T value) {
        // Step 1. Creating Header
        MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
        header.add("playerId", String.valueOf(player));
        header.add("Content-Type", "application/json");
        // Step 2. Generating request
        return new HttpEntity<T>(value, header);
    }

    public <T> HttpEntity<T> signGame(GameSessionKey session, T value) {
        // Step 1. Creating Header
        MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
        header.add("playerId", String.valueOf(player));
        header.add("sessionId", String.valueOf(session));
        header.add("Content-Type", "application/json");
        // Step 2. Generating request
        return new HttpEntity<T>(value, header);
    }

    public void listen(GameSessionKey session, GameSessionListener sessionListener) {
        playerListenersManager.listen(session, sessionListener);
    }

    public void listen(GameConstruction construction, GameSessionListener sessionListener) {
        playerListenersManager.listen(construction, sessionListener);
    }

    public void close() {
        playerListenersManager.close();
    }

}
