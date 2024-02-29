package com.zyneonstudios.application.backend.utils;

import fr.flowarg.flowlogger.ILogger;
import live.nerotv.shademebaby.logger.Logger;

import java.nio.file.Path;

public class ZLogger extends Logger implements ILogger {

    private String prefix;
    private Path logPath;

    public ZLogger(String loggerName) {
        super(loggerName);
        prefix = loggerName;
    }

    @Override
    public void err(String s) {
        error(s);
    }

    @Override
    public void info(String s) {
        log(s);
    }

    @Override
    public void warn(String s) {
        log("(WARNING) "+s);
    }

    @Override
    public void infoColor(EnumLogColor enumLogColor, String s) {
        log(s);
    }

    @Override
    public void printStackTrace(String s, Throwable throwable) {
        throw new RuntimeException(s,throwable);
    }

    @Override
    public void printStackTrace(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    @Override
    public Path getLogPath() {
        return logPath;
    }

    @Override
    public void setLogPath(Path path) {
        logPath = path;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @SuppressWarnings("unused")
    public void setPrefix(String p) {
        prefix = p;
    }
}
