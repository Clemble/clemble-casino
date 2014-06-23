package com.clemble.casino.server.web.player.registration;

import static com.google.common.base.Preconditions.checkNotNull;

import com.clemble.casino.server.ExternalController;
import com.clemble.casino.server.player.registration.ServerProfileRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.clemble.casino.player.PlayerProfile;
import com.clemble.casino.player.SocialAccessGrant;
import com.clemble.casino.player.SocialConnectionData;
import com.clemble.casino.web.mapping.WebMapping;
import com.clemble.casino.web.player.PlayerWebMapping;

// TODO Consider deprecated

@Controller
public class ServerProfileRegistrationController implements ServerProfileRegistrationService, ExternalController {

    final private ServerProfileRegistrationService registrationService;

    public ServerProfileRegistrationController(final ServerProfileRegistrationService playerProfileRegistrationServerService) {
        this.registrationService = checkNotNull(playerProfileRegistrationServerService);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = PlayerWebMapping.REGISTRATION, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody PlayerProfile create(@RequestBody final PlayerProfile playerProfile) {
        return registrationService.create(playerProfile);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = PlayerWebMapping.REGISTRATION_SOCIAL, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody PlayerProfile create(@RequestBody SocialConnectionData socialConnectionData) {
        return registrationService.create(socialConnectionData);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = PlayerWebMapping.REGISTRATION_GRANT, produces = WebMapping.PRODUCES)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody PlayerProfile create(@RequestBody SocialAccessGrant accessGrant) {
        return registrationService.create(accessGrant);
    }

}