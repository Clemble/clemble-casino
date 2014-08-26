package com.clemble.casino.server.game.repository;

import com.clemble.casino.game.GameRecord;
import com.clemble.casino.game.action.GameEventRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MadeMoveRepository {

    final private GameRecordRepository recordRepository;

    public MadeMoveRepository(GameRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public void save(String sessionKey, GameEventRecord move) {
        GameRecord record = recordRepository.findOne(sessionKey);
        record.getEventRecords().add(move);
        recordRepository.save(record);
    }
}