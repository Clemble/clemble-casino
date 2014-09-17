package com.clemble.casino.integration.player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.client.goal.GoalOperations;
import com.clemble.casino.game.construction.event.GameInitiationCreatedEvent;
import com.clemble.casino.integration.game.IntegrationAutoGameConstructionService;
import com.clemble.casino.integration.game.IntegrationAvailabilityGameConstructionService;
import com.clemble.casino.integration.payment.IntegrationPlayerAccountService;
import com.clemble.casino.payment.service.PaymentTransactionOperations;
import com.clemble.casino.payment.service.PlayerAccountService;
import com.clemble.casino.player.service.*;
import com.clemble.casino.server.connection.controller.PlayerConnectionServiceController;
import com.clemble.casino.server.game.construction.controller.AutoGameConstructionController;
import com.clemble.casino.server.game.construction.controller.AvailabilityGameConstructionController;
import com.clemble.casino.server.game.construction.controller.GameInitiationServiceController;
import com.clemble.casino.server.payment.controller.PaymentTransactionServiceController;
import com.clemble.casino.server.payment.controller.PlayerAccountServiceController;
import com.clemble.casino.server.presence.controller.PlayerPresenceServiceController;
import com.clemble.casino.server.presence.controller.PlayerSessionServiceController;
import com.clemble.casino.server.profile.controller.PlayerImageServiceController;
import com.clemble.casino.server.profile.controller.PlayerProfileServiceController;
import org.springframework.web.client.RestTemplate;

import com.clemble.casino.client.ClembleCasinoOperations;
import com.clemble.casino.client.event.EventListener;
import com.clemble.casino.client.event.EventListenerOperations;
import com.clemble.casino.client.event.EventTypeSelector;
import com.clemble.casino.client.game.GameActionOperations;
import com.clemble.casino.client.game.GameActionOperationsFactory;
import com.clemble.casino.client.game.GameActionTemplateFactory;
import com.clemble.casino.client.game.GameConstructionOperations;
import com.clemble.casino.client.game.GameConstructionTemplate;
import com.clemble.casino.integration.payment.IntegrationPaymentTransactionService;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construction.service.AutoGameConstructionService;
import com.clemble.casino.game.service.GameActionService;
import com.clemble.casino.game.configuration.service.GameConfigurationService;
import com.clemble.casino.game.service.GameRecordService;
import com.clemble.casino.integration.event.EventListenerOperationsFactory;
import com.clemble.casino.registration.PlayerCredential;
import com.clemble.casino.player.PlayerSession;
import com.clemble.casino.registration.PlayerToken;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IntegrationClembleCasinoOperations implements ClembleCasinoOperations {

    /**
     * Generated
     */
    private static final long serialVersionUID = -4160641502466429770L;

    final private String player;
    final private PlayerSession session;
    final private EventListenerOperations listenerOperations;

    final private AutoGameConstructionService constructionService;
    final private GameConstructionOperations gameConstructors;
    final private PlayerPresenceService playerPresenceOperations;
    final private PlayerProfileService profileOperations;
    final private PlayerImageService imageOperations;
    final private PlayerConnectionService connectionOperations;
    final private PlayerSessionService playerSessionOperations;
    final private PlayerAccountService accountService;
    final private PaymentTransactionOperations paymentTransactionOperations;
    final private GameActionOperationsFactory actionOperationsFactory;
    final private GameRecordService recordService;

    final private GoalOperations goalOperations;

    public IntegrationClembleCasinoOperations(
        final String host,
        final ObjectMapper objectMapper,
        final PlayerToken playerIdentity,
        final PlayerCredential credential,
        final PlayerProfileServiceController playerProfileService,
        final PlayerImageServiceController imageService,
        final PlayerConnectionServiceController playerConnectionService,
        final PlayerSessionServiceController sessionOperations,
        final PlayerAccountServiceController accountServiceController,
        final PaymentTransactionServiceController paymentTransactionService,
        final EventListenerOperationsFactory listenerOperationsFactory,
        final PlayerPresenceServiceController playerPresenceService,
        final AutoGameConstructionController gameConstructionService,
        final AvailabilityGameConstructionController availabilityConstructionService,
        final GameInitiationServiceController initiationService,
        final GameConfigurationService specificationService,
        final GameActionService actionService,
        final GameRecordService recordService,
        final GoalOperations goalOperations
    ) {
        this.player = playerIdentity.getPlayer();
        this.playerSessionOperations = new IntegrationPlayerSessionService(player, sessionOperations);
        this.session = checkNotNull(playerSessionOperations.create());
        this.listenerOperations = listenerOperationsFactory.construct(player, host, objectMapper);

        this.playerPresenceOperations = new IntegrationPlayerPresenceTemplate(player, playerPresenceService);

        this.profileOperations = new IntegrationPlayerProfileService(player, playerProfileService);
        this.imageOperations = new IntegrationPlayerImageService(player, imageService);
        this.connectionOperations = new IntegrationPlayerConnectionService(player, playerConnectionService);
        this.paymentTransactionOperations = new PaymentTransactionOperations(new IntegrationPaymentTransactionService(player, paymentTransactionService));
        this.accountService = new IntegrationPlayerAccountService(player, accountServiceController);

        this.constructionService = new IntegrationAutoGameConstructionService(player, checkNotNull(gameConstructionService));

        this.gameConstructors = new GameConstructionTemplate(player, constructionService, new IntegrationAvailabilityGameConstructionService(player, availabilityConstructionService), initiationService, specificationService, listenerOperations);
        this.actionOperationsFactory = new GameActionTemplateFactory(player, listenerOperations, actionService);

        this.listenerOperations.subscribe(new EventTypeSelector(GameInitiationCreatedEvent.class), new EventListener<GameInitiationCreatedEvent>() {
            @Override
            public void onEvent(GameInitiationCreatedEvent event) {
                // Step 1. Fetching session key
                String sessionKey = event.getSessionKey();
                initiationService.confirm(player, sessionKey);
            }
        });
        this.recordService = recordService;

        this.goalOperations = goalOperations;
    }

    @Override
    public PlayerSessionService sessionOperations() {
        return playerSessionOperations;
    }

    @Override
    public PlayerProfileService profileOperations() {
        return profileOperations;
    }

    @Override
    public PlayerImageService imageOperations() {
        return imageOperations;
    }

    @Override
    public PlayerConnectionService connectionOperations() {
        return connectionOperations;
    }

    @Override
    public PlayerAccountService accountService() {
        return accountService;
    }

    @Override
    public PaymentTransactionOperations paymentOperations() {
        return paymentTransactionOperations;
    }

    @Override
    public GoalOperations goalOperations() {
        return goalOperations;
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public GameConstructionOperations gameConstructionOperations() {
        return gameConstructors;
    }

    @Override
    public void close() {
        listenerOperations.close();
    }

    @Override
    public PlayerPresenceService presenceOperations() {
        return playerPresenceOperations;
    }

    @Override
    public <State extends GameState> GameActionOperations<State> gameActionOperations(String session) {
        return (GameActionOperations<State>) actionOperationsFactory.<State> construct(session);
    }

    @Override
    public RestTemplate getRestTemplate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

    @Override
    public EventListenerOperations listenerOperations() {
        return listenerOperations;
    }

    @Override
    public GameRecordService gameRecordOperations() {
        return recordService;
    }

}
