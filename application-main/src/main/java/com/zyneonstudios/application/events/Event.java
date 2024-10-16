package com.zyneonstudios.application.events;

import java.util.UUID;

public interface Event {

    //TODO need more events pls dont forget
    //TODO really pls stop making everything via request strings
    //TODO ~a friend of this nero guy using his computer

    UUID getUUID();

    boolean execute();
}