package com.example.user.cheahweiseng;

import com.example.user.cheahweiseng.Class.LodgeUser;

/**
 * Created by Lee Zi Xiang on 28/11/2017.
 */

public class LoginUser {

    private static LodgeUser lodgeUser = new LodgeUser();

    public static void Login(LodgeUser lodgeUser) {
        LoginUser.lodgeUser = lodgeUser;
    }

    public static LodgeUser getLodgeUser() {
        return lodgeUser;
    }

    public static void Logout() {
        lodgeUser = new LodgeUser();
    }
}
