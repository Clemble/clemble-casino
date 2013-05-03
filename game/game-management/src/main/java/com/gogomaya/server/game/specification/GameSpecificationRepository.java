package com.gogomaya.server.game.specification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.game.specification.SpecificationName;

@Repository
public interface GameSpecificationRepository extends JpaRepository<GameSpecification, SpecificationName> {

}