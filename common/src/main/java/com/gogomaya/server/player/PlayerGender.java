package com.gogomaya.server.player;

import static com.google.common.base.Strings.isNullOrEmpty;

public enum PlayerGender {
    M, W;

    public static PlayerGender parse(String gender) {
        // Step 1. Sanity check
        if (isNullOrEmpty(gender))
            return null;
        // Step 2. Checking for male
        gender = gender.toLowerCase().trim();
        if (gender.startsWith("m")) {
            return PlayerGender.M;
        } else if (gender.startsWith("w") || gender.startsWith("f")) {
            return PlayerGender.W;
        }
        // Step 3. If you can't parse it consider it unknown
        return null;
    }

}