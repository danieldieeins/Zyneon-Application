package com.zyneonstudios.application.backend.utils.backend;

import com.zyneonstudios.application.Application;

public class AuthResolver {

    public AuthResolver() {}

    public void preAuth() {}

    public void postAuth(String username) {
        Application.getFrame().executeJavaScript("login('"+username+"');");
        Application.getFrame().executeJavaScript("unmessage();");
        Application.getFrame().executeJavaScript("syncProfileSettings();");
    }
}