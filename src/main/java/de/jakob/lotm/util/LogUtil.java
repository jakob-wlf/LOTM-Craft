package de.jakob.lotm.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    private final Logger logger;

    public LogUtil(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void warn(String message) {
        logger.log(Level.WARNING, message);
    }

    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    public void debug(String message) {
        logger.log(Level.INFO, "[DEBUG] " + message);
    }
}
