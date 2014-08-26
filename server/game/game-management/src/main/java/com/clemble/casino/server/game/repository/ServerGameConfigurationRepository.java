package com.clemble.casino.server.game.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.configuration.ServerGameConfiguration;
import com.clemble.casino.game.configuration.GameConfigurationKey;

@Repository
public interface ServerGameConfigurationRepository extends MongoRepository<ServerGameConfiguration, GameConfigurationKey> {

    public List<ServerGameConfiguration> findByConfigurationKeyGame(@Param("game") Game game);

}
