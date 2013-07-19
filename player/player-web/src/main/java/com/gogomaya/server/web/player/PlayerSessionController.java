package com.gogomaya.server.web.player;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gogomaya.server.error.GogomayaError;
import com.gogomaya.server.error.GogomayaException;
import com.gogomaya.server.player.notification.PlayerNotificationRegistry;
import com.gogomaya.server.player.security.PlayerSession;
import com.gogomaya.server.player.state.PlayerStateManager;
import com.gogomaya.server.repository.player.PlayerSessionRepository;
import com.gogomaya.server.web.mapping.PlayerWebMapping;

@Controller
public class PlayerSessionController {

    final PlayerNotificationRegistry notificationRegistry;
    final PlayerSessionRepository sessionRepository;
    final PlayerStateManager stateManager;

    public PlayerSessionController(final PlayerNotificationRegistry notificationRegistry,
            final PlayerSessionRepository sessionRepository,
            final PlayerStateManager stateManager) {
        this.notificationRegistry = checkNotNull(notificationRegistry);
        this.sessionRepository = checkNotNull(sessionRepository);
        this.stateManager = checkNotNull(stateManager);
    }

    @RequestMapping(method = RequestMethod.POST, value = PlayerWebMapping.PLAYER_SESSIONS, produces = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody
    PlayerSession post(@RequestHeader("playerId") long requesterId, @PathVariable("playerId") long playerId) {
        // Step 0. Checking player is the one who trying to modify it
        if (requesterId != playerId)
            throw GogomayaException.fromError(GogomayaError.PlayerNotSessionOwner);
        // Step 1. Generated player session
        PlayerSession session = new PlayerSession().setPlayerId(playerId).setServer(notificationRegistry.findNotificationServer(playerId));
        // Step 2. Providing result as a Session data
        Date expireTime = stateManager.markAlive(playerId);
        session.setExpirationTime(expireTime);
        session = sessionRepository.saveAndFlush(session);
        // Step 3. Returning session identifier
        return session;
    }

    @RequestMapping(method = RequestMethod.PUT, value = PlayerWebMapping.PLAYER_SESSIONS_SESSION, produces = "application/json")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public @ResponseBody
    PlayerSession refresh(@RequestHeader("playerId") long requesterId, @PathVariable("playerId") long playerId, @PathVariable("sessionId") long sessionId) {
        // Step 1. Fetching session
        PlayerSession playerSession = get(requesterId, playerId, sessionId);
        // Step 2. Sanity check
        if (playerSession.expired())
            throw GogomayaException.fromError(GogomayaError.PlayerSessionClosed);
        Date expireTime = stateManager.refresh(playerId);
        playerSession.setExpirationTime(expireTime);
        playerSession = sessionRepository.saveAndFlush(playerSession);
        return playerSession;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = PlayerWebMapping.PLAYER_SESSIONS_SESSION, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    PlayerSession end(@RequestHeader("playerId") long requesterId, @PathVariable("playerId") long playerId, @PathVariable("sessionId") long sessionId) {
        // Step 1. Fetching player session
        PlayerSession playerSession = get(requesterId, playerId, sessionId);
        if (playerSession.expired())
            throw GogomayaException.fromError(GogomayaError.PlayerSessionClosed);
        // Step 2. Notifying all listeners of the state change
        stateManager.markLeft(playerId);
        // Step 3. Marking player state as ended
        playerSession.markExpired();
        return sessionRepository.saveAndFlush(playerSession);
    }

    @RequestMapping(method = RequestMethod.GET, value = PlayerWebMapping.PLAYER_SESSIONS, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<PlayerSession> list(@RequestHeader("playerId") long requesterId, @PathVariable("playerId") long playerId) {
        // Step 0. Checking player is the one who trying to modify it
        if (requesterId != playerId)
            throw GogomayaException.fromError(GogomayaError.PlayerNotSessionOwner);
        // Step 1. Reading specific session
        List<PlayerSession> playerSessions = sessionRepository.findByPlayerId(playerId);
        // Step 2. Returning read sessions
        return playerSessions;
    }

    @RequestMapping(method = RequestMethod.GET, value = PlayerWebMapping.PLAYER_SESSIONS_SESSION, produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    PlayerSession get(@RequestHeader("playerId") long requesterId, @PathVariable("playerId") long playerId, @PathVariable("sessionId") long sessionId) {
        // Step 0. Checking player is the one who trying to modify it
        if (requesterId != playerId)
            throw GogomayaException.fromError(GogomayaError.PlayerNotSessionOwner);
        // Step 2. Reading specific session
        PlayerSession playerSession = sessionRepository.findOne(sessionId);
        if (playerSession.getPlayerId() != playerId)
            throw GogomayaException.fromError(GogomayaError.PlayerNotSessionOwner);
        // Step 3. Returning session
        return playerSession;
    }
}