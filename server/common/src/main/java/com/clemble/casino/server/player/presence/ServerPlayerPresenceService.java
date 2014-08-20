package com.clemble.casino.server.player.presence;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.clemble.casino.player.PlayerPresence;
import com.clemble.casino.server.ServerService;

public interface ServerPlayerPresenceService extends ServerService {

    public boolean isAvailable(String player);

    public boolean areAvailable(Collection<String> players);

    public PlayerPresence getPresence(String player);

    public List<PlayerPresence> getPresences(Collection<String> presences);

    public Date markAvailable(String player);

    public Date markOnline(String player);

    public void markOffline(String player);

    public boolean markPlaying(String player, String sessionKey);

    public boolean markPlaying(Collection<String> players, String sessionKey);

}
