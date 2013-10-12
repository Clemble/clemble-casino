package com.clemble.casino.server.game.aspect.time;

import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.server.game.action.GameEventTaskExecutor;
import com.clemble.casino.server.game.aspect.GameAspect;
import com.clemble.casino.server.game.aspect.GameAspectFactory;

public class GameTimeAspectFactory implements GameAspectFactory {

    final private GameEventTaskExecutor eventTaskExecutor;

    public GameTimeAspectFactory(GameEventTaskExecutor eventTaskExecutor) {
        this.eventTaskExecutor = eventTaskExecutor;
    }

    @Override
    public <T extends GameState> GameAspect<T> construct(GameInitiation initiation) {
        return new GameTimeAspect<>(new SessionTimeTask(initiation), eventTaskExecutor);
    }

}