package io.github.djxy.permissionmanager.logger;

import com.google.common.base.Preconditions;

/**
 * Created by Samuel on 2016-08-17.
 */
public class Logger {

    private static LoggerMode loggerMode = LoggerMode.NO_LOG;

    public static LoggerMode getLoggerMode() {
        return loggerMode;
    }

    public static void setLoggerMode(LoggerMode mode){
        Preconditions.checkNotNull(mode);

        loggerMode = mode;
    }

    private final Class clazz;

    public Logger(Class clazz) {
        this.clazz = clazz;
    }

    public void info(String message){
        if(loggerMode == LoggerMode.NO_LOG)
            return;
        else if(loggerMode == LoggerMode.DEBUG_SERVER)
            org.slf4j.LoggerFactory.getLogger(clazz).info(message);
        else if(loggerMode == LoggerMode.DEBUG_IDE) {
            synchronized (message) {
                System.out.println("[" + System.currentTimeMillis() + "][" + clazz.getSimpleName() + "][INFO]: " + message);
            }
        }
    }

    public void warn(String message){
        if(loggerMode == LoggerMode.NO_LOG)
            return;
        else if(loggerMode == LoggerMode.DEBUG_SERVER)
            org.slf4j.LoggerFactory.getLogger(clazz).warn(message);
        else if(loggerMode == LoggerMode.DEBUG_IDE){
            synchronized (message) {
                System.out.println("["+System.currentTimeMillis()+"]["+clazz.getSimpleName()+"][WARN]: "+message);
            }
        }
    }

    public void error(String message){
        if(loggerMode == LoggerMode.NO_LOG)
            return;
        else if(loggerMode == LoggerMode.DEBUG_SERVER)
            org.slf4j.LoggerFactory.getLogger(clazz).error(message);
        else if(loggerMode == LoggerMode.DEBUG_IDE){
            synchronized (message) {
                System.err.println("["+System.currentTimeMillis()+"]["+clazz.getSimpleName()+"][ERROR]: "+message);
            }
        }
    }

}
