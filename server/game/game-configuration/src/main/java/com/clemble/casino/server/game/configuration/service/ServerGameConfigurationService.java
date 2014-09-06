package com.clemble.casino.server.game.configuration.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import com.clemble.casino.server.game.configuration.ServerGameConfiguration;
import com.clemble.casino.server.game.configuration.repository.ServerGameConfigurationRepository;
import com.clemble.casino.game.service.GameConfigurationService;
import com.clemble.casino.game.configuration.GameConfiguration;
import com.clemble.casino.game.configuration.GameConfigurations;

public class ServerGameConfigurationService implements GameConfigurationService {

    final private ServerGameConfigurationRepository specificationRepository;

    public ServerGameConfigurationService(ServerGameConfigurationRepository specificationRepository) {
        this.specificationRepository = checkNotNull(specificationRepository);
    }

    public boolean isValid(GameConfiguration configuration) {
        return true;
    }

    @Override
    public GameConfigurations getConfigurations() {
        List<GameConfiguration> configurations = new ArrayList<>();
        for(ServerGameConfiguration configuration: specificationRepository.findAll())
            configurations.add(configuration.getConfiguration());
        return new GameConfigurations(configurations);
    }


}