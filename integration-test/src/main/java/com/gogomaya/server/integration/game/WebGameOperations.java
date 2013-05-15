package com.gogomaya.server.integration.game;

import static com.google.common.base.Preconditions.checkNotNull;

import com.gogomaya.server.game.action.GameTable;
import com.gogomaya.server.game.configuration.SelectSpecificationOptions;
import com.gogomaya.server.game.specification.GameSpecification;
import com.gogomaya.server.game.tictactoe.action.TicTacToeState;
import com.gogomaya.server.integration.player.Player;
import com.gogomaya.server.web.active.session.GameTableMatchController;
import com.gogomaya.server.web.game.configuration.GameConfigurationManagerController;

public class WebGameOperations extends AbstractGameOperation {

    final private GameConfigurationManagerController configuartionManagerController;

    final private GameTableMatchController<TicTacToeState> matchController;
    
    public WebGameOperations(GameConfigurationManagerController configurationManagerController, GameTableMatchController<TicTacToeState> matchController) {
        this.configuartionManagerController = checkNotNull(configurationManagerController);
        this.matchController = checkNotNull(matchController);
    }

    @Override
    public SelectSpecificationOptions getOptions() {
        return getOptions(null);
    }

    @Override
    public SelectSpecificationOptions getOptions(Player player) {
        return (SelectSpecificationOptions) configuartionManagerController.get(player != null ? player.getPlayerId() : -1L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GameTable<?>> T start(Player player, GameSpecification gameSpecification) {
        return (T) matchController.match(player.getPlayerId(), gameSpecification);
    }

}
