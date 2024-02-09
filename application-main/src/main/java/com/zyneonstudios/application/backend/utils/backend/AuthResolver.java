package com.zyneonstudios.application.backend.utils.backend;

import com.zyneonstudios.application.Application;

public class AuthResolver {

    public AuthResolver() {}

    public void preAuth() {}

    public void postAuth() {
        Application.getFrame().getBrowser().reload();
    }
}
