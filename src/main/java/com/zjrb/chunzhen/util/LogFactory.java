package com.zjrb.chunzhen.util;

/**
 * Created by blw on 2017/9/19.
 * @author blw
 * 日志工厂
 */
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogFactory {
    private static final Logger logger;
    static {
        logger = Logger.getLogger("stdout");
        logger.setLevel(Level.DEBUG);
    }

    public static void log(String info, Level level, Throwable ex) {
        logger.log(level, info, ex);
    }

    public static Level  getLogLevel(){
        return logger.getLevel();
    }

}