package com.clemble.casino.integration.data;

import com.clemble.casino.player.PlayerCategory;
import com.clemble.casino.player.PlayerGender;
import com.clemble.casino.player.PlayerProfile;

public class DataGenerator {

    public static PlayerProfile randomProfile() {
        return new PlayerProfile()
            .setCategory(PlayerCategory.Amateur)
            .setFirstName("Anton")
            .setLastName("Oparin")
            .setGender(PlayerGender.M)
            .setNickName("mavarazy");
    }
}