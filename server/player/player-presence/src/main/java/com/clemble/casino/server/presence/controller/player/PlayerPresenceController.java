package com.clemble.casino.server.presence.controller.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.clemble.casino.server.ExternalController;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.clemble.casino.player.PlayerPresence;
import com.clemble.casino.player.service.PlayerPresenceService;
import com.clemble.casino.server.player.presence.ServerPlayerPresenceService;

import static com.clemble.casino.web.player.PlayerWebMapping.*;

@Controller
public class PlayerPresenceController implements PlayerPresenceService, ExternalController {

    final private ServerPlayerPresenceService presenceServerService;

    public PlayerPresenceController(ServerPlayerPresenceService playerPresenceServerService) {
        this.presenceServerService = checkNotNull(playerPresenceServerService);
    }

    @Override
    @RequestMapping(value = PRESENCE_PLAYER, method = RequestMethod.GET, produces = PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody PlayerPresence getPresence(@PathVariable("player") String player) {
        return presenceServerService.getPresence(player);
    }

    @Override
    @RequestMapping(value = PRESENCE, method = RequestMethod.GET, produces = PRODUCES)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody List<PlayerPresence> getPresences(@RequestParam(required = true, value = PLAYER_PRESENCES_PARAM) List<String> players) {
        return presenceServerService.getPresences(players);
    }

}