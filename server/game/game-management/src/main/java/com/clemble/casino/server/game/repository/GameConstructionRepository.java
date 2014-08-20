package com.clemble.casino.server.game.repository;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clemble.casino.game.construct.GameConstruction;

@Repository
@Transactional
public interface GameConstructionRepository extends JpaRepository<GameConstruction, String> {

    @Override
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    public <S extends GameConstruction> List<S> save(Iterable<S> entities);

}
