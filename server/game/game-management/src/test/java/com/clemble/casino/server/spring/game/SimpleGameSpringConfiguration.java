package com.clemble.casino.server.spring.game;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.MatchGameContext;
import com.clemble.casino.game.MatchGameRecord;
import com.clemble.casino.game.PotGameContext;
import com.clemble.casino.game.PotGameRecord;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.game.specification.MatchGameConfiguration;
import com.clemble.casino.game.specification.PotGameConfiguration;
import com.clemble.casino.server.game.action.GameManagerFactory;
import com.clemble.casino.server.game.action.GameStateFactory;
import com.clemble.casino.server.game.action.ServerGameProcessorFactory;
import com.clemble.casino.server.game.aspect.MatchGameAspectFactory;
import com.clemble.casino.server.player.notification.PlayerNotificationService;
import com.clemble.casino.server.repository.game.MatchGameRecordRepository;
import com.clemble.casino.server.repository.game.PotGameRecordRepository;
import com.clemble.casino.server.repository.game.ServerGameConfigurationRepository;

@Ignore
@Configuration
@Import(GameManagementSpringConfiguration.class)
@SuppressWarnings({ "rawtypes" })
public class SimpleGameSpringConfiguration {

    @Bean
    public GameManagerFactory gameProcessor(PotGameRecordRepository potRepository,
            GameStateFactory<GameState> stateFactory,
            ServerGameProcessorFactory<MatchGameConfiguration, MatchGameContext, MatchGameRecord> processorFactory,
            ServerGameProcessorFactory<PotGameConfiguration, PotGameContext, PotGameRecord> potProcessorFactory,
            MatchGameRecordRepository sessionRepository,
            ServerGameConfigurationRepository configurationRepository,
            @Qualifier("playerNotificationService") PlayerNotificationService notificationService) {
        return new GameManagerFactory(potRepository, stateFactory, processorFactory, potProcessorFactory, sessionRepository, configurationRepository, notificationService);
    }

    @Bean
    public ServerGameProcessorFactory<MatchGameConfiguration, MatchGameContext, MatchGameRecord> processorFactory() {
        return new ServerGameProcessorFactory<>(MatchGameAspectFactory.class);
    }

    @Bean
    public GameStateFactory fakeStateFactory() {
        return new GameStateFactory() {

            @Override
            public Game getGame() {
                return Game.num;
            }

            @Override
            public GameState constructState(GameInitiation initiation, MatchGameContext context) {
                // TODO Auto-generated method stub
                return null;
            }

        };
    }

}
