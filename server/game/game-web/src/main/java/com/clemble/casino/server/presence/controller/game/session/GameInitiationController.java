package com.clemble.casino.server.presence.controller.game.session;

import static com.clemble.casino.utils.Preconditions.checkNotNull;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.clemble.casino.game.Game;
import com.clemble.casino.game.construct.GameInitiation;
import com.clemble.casino.game.service.GameInitiationService;
import com.clemble.casino.server.ExternalController;
import com.clemble.casino.server.game.construct.ServerGameInitiationService;
import static com.clemble.casino.game.GameWebMapping.*;

@Controller
public class GameInitiationController implements GameInitiationService, ExternalController {

    final private ServerGameInitiationService initiationService;

    public GameInitiationController(ServerGameInitiationService initiationService) {
        this.initiationService = checkNotNull(initiationService);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = INITIATION_READY, produces = PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody GameInitiation confirm(@PathVariable("session") final String session, @PathVariable("playerId") final String player) {
        return initiationService.confirm(session, player);
    }

}
