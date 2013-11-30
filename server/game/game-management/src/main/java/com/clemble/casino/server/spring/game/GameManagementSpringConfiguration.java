package com.clemble.casino.server.spring.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import com.clemble.casino.event.Event;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.game.id.GameIdGenerator;
import com.clemble.casino.game.id.UUIDGameIdGenerator;
import com.clemble.casino.server.game.action.GameEventTaskExecutor;
import com.clemble.casino.server.game.aspect.GameManagementAspect;
import com.clemble.casino.server.game.aspect.bet.GameBetAspectFactory;
import com.clemble.casino.server.game.aspect.management.GameSequenceManagementAspect;
import com.clemble.casino.server.game.aspect.outcome.GameOutcomeAspect;
import com.clemble.casino.server.game.aspect.price.GamePriceAspectFactory;
import com.clemble.casino.server.game.aspect.security.GameSecurityAspectFactory;
import com.clemble.casino.server.game.aspect.time.GameTimeAspectFactory;
import com.clemble.casino.server.game.configuration.GameSpecificationRegistry;
import com.clemble.casino.server.game.construct.GameConstructionServerService;
import com.clemble.casino.server.game.construct.GameInitiatorService;
import com.clemble.casino.server.game.construct.SimpleGameConstructionServerService;
import com.clemble.casino.server.payment.PaymentTransactionServerService;
import com.clemble.casino.server.player.account.PlayerAccountServerService;
import com.clemble.casino.server.player.lock.PlayerLockService;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.clemble.casino.server.player.presence.PlayerPresenceServerService;
import com.clemble.casino.server.repository.game.GameConstructionRepository;
import com.clemble.casino.server.spring.common.CommonSpringConfiguration;
import com.clemble.casino.server.spring.common.SpringConfiguration;
import com.clemble.casino.server.spring.payment.PaymentCommonSpringConfiguration;
import com.clemble.casino.server.spring.player.PlayerCommonSpringConfiguration;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@Configuration
@Import(value = { CommonSpringConfiguration.class, GameJPASpringConfiguration.class, PaymentCommonSpringConfiguration.class,
        PlayerCommonSpringConfiguration.class, GameManagementSpringConfiguration.GameTimeAspectConfiguration.class,
        GameManagementSpringConfiguration.Test.class })
public class GameManagementSpringConfiguration implements SpringConfiguration {

    @Autowired
    @Qualifier("paymentTransactionService")
    public PaymentTransactionServerService paymentTransactionService;

    @Autowired
    @Qualifier("playerStateManager")
    public PlayerPresenceServerService playerStateManager;

    @Bean
    public GameIdGenerator gameIdGenerator() {
        return new UUIDGameIdGenerator();
    }

    @Bean
    public GamePriceAspectFactory gamePriceAspectFactory() {
        return new GamePriceAspectFactory();
    }

    @Bean
    public GameBetAspectFactory gameBetAspectFactory() {
        return new GameBetAspectFactory();
    }

    @Bean
    public GameSecurityAspectFactory gameSecurityAspectFactory() {
        return new GameSecurityAspectFactory();
    }

    @Bean
    public GameOutcomeAspect gameOutcomeAspectFactory(PlayerPresenceServerService playerStateManager, PaymentTransactionServerService paymentTransactionService) {
        return new GameOutcomeAspect(playerStateManager, paymentTransactionService);
    }

    @Bean
    @Autowired
    public GameManagementAspect gameManagementAspectFactory(GameIdGenerator idGenerator,
            GameInitiatorService initiatorService,
            GameConstructionRepository constructionRepository){
        return new GameSequenceManagementAspect(idGenerator, initiatorService, constructionRepository);
    }

    @Bean
    public GameSpecificationRegistry gameSpecificationRegistry() {
        return new GameSpecificationRegistry();
    }

    /**
     * Needed to separate this way, since BeanPostProcessor is loaded prior to any other configuration, Spring tries to load whole configuration, but some
     * dependencies are naturally missing - like Repositories
     * 
     * @author mavarazy
     * 
     */
    @Configuration
    public static class GameTimeAspectConfiguration {

        @Bean
        public GameTimeAspectFactory gameTimeAspectFactory() {
            return new GameTimeAspectFactory(eventTaskExecutor());
        }

        @Bean
        public GameEventTaskExecutor eventTaskExecutor() {
            ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder().setNameFormat("[GETE] task-executor - %d");
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5, threadFactoryBuilder.build());
            return new GameEventTaskExecutor(executorService);
        }
    }

    @Configuration
    @Profile(value = { UNIT_TEST })
    public static class Test {

        @Autowired
        public PlayerLockService playerLockService;

        @Autowired
        public PlayerPresenceServerService playerStateManager;

        @Autowired
        public PlayerNotificationService<Event> playerNotificationService;

        @Autowired
        public GameConstructionRepository constructionRepository;

        @Autowired
        public PlayerAccountServerService playerAccountService;

        @Autowired
        public GameIdGenerator gameIdGenerator;

        @Bean
        public GameConstructionServerService gameConstructionService() {
            return new SimpleGameConstructionServerService(gameIdGenerator, playerAccountService, playerNotificationService, constructionRepository,
                    initiatorService(), playerLockService, playerStateManager);
        }

        @Bean
        public GameInitiatorService initiatorService() {
            return new GameInitiatorService() {
                @Override
                public void initiate(GameConstruction construction) {
                }

                @Override
                public boolean initiate(GameInitiation initiation) {
                    return true;
                }
            };
        }

    }

}
