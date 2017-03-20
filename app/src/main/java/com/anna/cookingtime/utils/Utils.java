package com.anna.cookingtime.utils;

import com.anna.cookingtime.models.User;

/**
 * Created by iva on 17.03.17.
 */

public class Utils {

    private static User user;

    private static boolean isRootFragment = true;

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }

    public static boolean isRootFragment() {
        return isRootFragment;
    }

    public static void setRootFragment(boolean rootFragment) {
        isRootFragment = rootFragment;
    }
}
