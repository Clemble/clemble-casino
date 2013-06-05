package com.gogomaya.server.web.player.session;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gogomaya.server.player.notification.PlayerNotificationRegistry;
import com.gogomaya.server.player.security.PlayerSession;
import com.gogomaya.server.player.session.PlayerSessionRepository;

@Controller
public class PlayerSessionController {

    final PlayerNotificationRegistry notificationRegistry;

    final PlayerSessionRepository sessionRepository;

    public PlayerSessionController(final PlayerNotificationRegistry notificationRegistry, final PlayerSessionRepository sessionRepository) {
        this.notificationRegistry = checkNotNull(notificationRegistry);
        this.sessionRepository = checkNotNull(sessionRepository);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/session/login", produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody PlayerSession startSession(@RequestHeader("playerId") long playerId) {
        // Step 1. Generated player session
        PlayerSession session = new PlayerSession().setPlayerId(playerId).setServer(notificationRegistry.findNotificationServer(playerId));
        // Step 2. Providing result as a Session data
        session = sessionRepository.saveAndFlush(session);
        // Step 3. Returning session identifier
        return session;
    }
}