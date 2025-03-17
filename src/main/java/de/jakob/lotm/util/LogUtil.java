package de.jakob.lotm.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

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
