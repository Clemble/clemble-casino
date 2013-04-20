package com.gogomaya.server.web.active.session;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaException;
import com.gogomaya.server.game.configuration.TicTacToeConfigurationManager;
import com.gogomaya.server.game.match.TicTacToeStateManager;
import com.gogomaya.server.game.table.TicTacToeTableRepository;
import com.gogomaya.server.game.tictactoe.TicTacToeSpecification;
import com.gogomaya.server.game.tictactoe.action.TicTacToeTable;

@Controller
public class TableMatchController {

    final private TicTacToeConfigurationManager configurationManager;

    final private TicTacToeStateManager matchingService;

    final private TicTacToeTableRepository tableRepository;

    public TableMatchController(final TicTacToeStateManager matchingService,
            final TicTacToeTableRepository sessionRepository,
            final TicTacToeConfigurationManager configurationManager) {
        this.matchingService = checkNotNull(matchingService);
        this.tableRepository = checkNotNull(sessionRepository);
        this.configurationManager = checkNotNull(configurationManager);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/active/session", produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody TicTacToeTable create(@RequestHeader("playerId") final long playerId, @RequestBody final TicTacToeSpecification gameSpecification) {
        // Step 1. Checking that provided specification was valid
        if (!configurationManager.getSpecificationOptions().valid(gameSpecification))
            throw GogomayaException.create(GogomayaError.GameSpecificationInvalid);
        // Step 2. Invoking actual matching service
        return matchingService.reserve(playerId, gameSpecification);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/active/session/{sessionId}", produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody TicTacToeTable get(@PathVariable("tableId") final long tableId) {
        return tableRepository.findOne(tableId);
    }
}
