package com.gogomaya.web.player;

import com.gogomaya.web.mapping.WebMapping;

public interface PlayerWebMapping extends WebMapping {

    final public static String PLAYER_PREFIX = "/player-web";

    final public static String PLAYER_PROFILE_REGISTRATION = "/player/";
    final public static String PLAYER_PROFILE_REGISTRATION_SOCIAL = "/social/";

    final public static String PLAYER_PROFILE = "/player/{playerId}";
    final public static String PLAYER_SOCIAL = "/social/{playerId}";

}
