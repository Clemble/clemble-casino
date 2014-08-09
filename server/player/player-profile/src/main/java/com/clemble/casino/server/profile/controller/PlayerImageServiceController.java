package com.clemble.casino.server.profile.controller;

import com.clemble.casino.player.service.PlayerImageServiceContract;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.clemble.casino.web.player.PlayerWebMapping.*;

/**
 * Created by mavarazy on 7/26/14.
 */
@RestController
public class PlayerImageServiceController implements PlayerImageServiceContract {


    @RequestMapping(value = MY_IMAGE, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] myImage(@CookieValue("player") String player) {
        return new byte[0];
    }


    @Override
    @RequestMapping(value = PLAYER_IMAGE, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] getImage(@PathVariable("player") String player) {
        return new byte[0];
    }

}
