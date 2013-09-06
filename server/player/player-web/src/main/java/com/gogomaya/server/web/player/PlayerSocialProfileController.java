package com.gogomaya.server.web.player;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gogomaya.player.SocialConnectionData;
import com.gogomaya.player.service.PlayerSocialProfileService;
import com.gogomaya.server.social.SocialConnectionDataAdapter;
import com.gogomaya.web.mapping.WebMapping;
import com.gogomaya.web.player.PlayerWebMapping;

public class PlayerSocialProfileController implements PlayerSocialProfileService {

    final private SocialConnectionDataAdapter socialConnectionDataAdapter;

    public PlayerSocialProfileController(SocialConnectionDataAdapter socialConnectionDataAdapter) {
        this.socialConnectionDataAdapter = checkNotNull(socialConnectionDataAdapter);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = PlayerWebMapping.PLAYER_SOCIAL, produces = WebMapping.PRODUCES)
    public @ResponseBody
    SocialConnectionData add(@PathVariable("playerId") long playerId, @RequestBody SocialConnectionData socialConnectionData) {
        return socialConnectionDataAdapter.add(playerId, socialConnectionData);
    }

}
