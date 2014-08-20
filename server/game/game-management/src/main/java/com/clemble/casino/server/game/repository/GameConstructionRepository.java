package com.clemble.casino.server.game.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.clemble.casino.game.construct.GameConstruction;

@Repository
public interface GameConstructionRepository extends MongoRepository<GameConstruction, String> {

    @Override
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    public <S extends GameConstruction> List<S> save(Iterable<S> entities);

}
