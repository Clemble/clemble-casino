package com.clemble.casino.server.web.game.session;

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

import com.clemble.casino.error.GogomayaError;
import com.clemble.casino.error.GogomayaException;
import com.clemble.casino.game.Game;
import com.clemble.casino.game.GameSessionKey;
import com.clemble.casino.game.GameState;
import com.clemble.casino.game.construct.GameConstruction;
import com.clemble.casino.game.construct.GameRequest;
import com.clemble.casino.game.event.schedule.InvitationResponseEvent;
import com.clemble.casino.game.service.GameConstructionService;
import com.clemble.casino.server.game.configuration.GameSpecificationRegistry;
import com.clemble.casino.server.game.construct.GameConstructionServerService;
import com.clemble.casino.server.game.notification.TableServerRegistry;
import com.clemble.casino.server.repository.game.GameConstructionRepository;
import com.clemble.casino.web.mapping.WebMapping;
import com.clemble.casino.event.ClientEvent;
import com.clemble.casino.web.game.GameWebMapping;

@Controller
public class GameConstructionController<State extends GameState> implements GameConstructionService {

    final private Game game;
    final private TableServerRegistry tableServerRegistry;
    final private GameSpecificationRegistry configurationManager;
    final private GameConstructionServerService constructionService;
    final private GameConstructionRepository constructionRepository;

    public GameConstructionController(
            final Game game,
            final GameConstructionRepository constructionRepository,
            final GameConstructionServerService matchingService,
            final GameSpecificationRegistry configurationManager,
            final TableServerRegistry tableServerRegistry) {
        this.game = checkNotNull(game);
        this.tableServerRegistry = checkNotNull(tableServerRegistry);
        this.constructionService = checkNotNull(matchingService);
        this.configurationManager = checkNotNull(configurationManager);
        this.constructionRepository = checkNotNull(constructionRepository);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = GameWebMapping.GAME_SESSIONS, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody GameConstruction construct(@RequestHeader("playerId") final String playerId, @RequestBody final GameRequest gameRequest) {
        // Step 1. Checking that provided specification was valid
        if (!configurationManager.getSpecificationOptions(gameRequest.getSpecification().getName().getGame()).valid(gameRequest.getSpecification()))
            throw GogomayaException.fromError(GogomayaError.GameSpecificationInvalid);
        // Step 2. Invoking actual matching service
        return constructionService.construct(gameRequest);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GameWebMapping.GAME_SESSIONS_CONSTRUCTION, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    GameConstruction getConstruct(@RequestHeader("playerId") final String playerId, @PathVariable("sessionId") final long session) {
        // Step 1. Searching for construction
        GameConstruction construction = constructionRepository.findOne(new GameSessionKey(game, session));
        // Step 2. Sending error in case resource not found
        if (construction == null)
            throw GogomayaException.fromError(GogomayaError.GameConstructionDoesNotExistent);
        // Step 3. Returning construction
        return construction;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GameWebMapping.GAME_SESSIONS_CONSTRUCTION_RESPONSES_PLAYER, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody ClientEvent getResponce(@RequestHeader("playerId") final String requester, @PathVariable("sessionId") final long session, @PathVariable("playerId") final String player) {
        return (ClientEvent) constructionRepository.findOne(new GameSessionKey(game, session)).getResponses().fetchAction(player);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = GameWebMapping.GAME_SESSIONS_CONSTRUCTION_RESPONSES, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody GameConstruction reply(@RequestHeader("playerId") final String playerId, @PathVariable("sessionId") long sessionId, @RequestBody final InvitationResponseEvent gameRequest) {
        // Step 1. Invoking actual matching service
        return constructionService.invitationResponsed(gameRequest);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET, value = GameWebMapping.GAME_SESSIONS_SERVER, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public String getServer(@RequestHeader("playerId") String playerId, @PathVariable("sessionId") long sessionId) {
        // Step 1. Fetching applicable server for the session
        return tableServerRegistry.findServer(sessionId);
    }
}