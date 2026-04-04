package com.slipgaji.controller;

import com.slipgaji.model.User;
import com.slipgaji.service.DatabaseService;

public class AuthController {
    private static User currentUser;

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong");
        }

        User user = DatabaseService.getInstance().authenticate(username.trim(), password);
        if (user == null) {
            throw new IllegalArgumentException("Username atau password salah");
        }

        currentUser = user;
        return user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isGeneralManager() {
        return currentUser != null && currentUser.isGeneralManager();
    }
}
