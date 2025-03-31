package de.jakob.lotm.util;

import de.jakob.lotm.LOTM;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void init() {
        LOTM.getInstance().saveDefaultConfig();

        config = LOTM.getInstance().getConfig();
        config.addDefault("npc_spawn_rate", 100);
        config.options().copyDefaults(true);
        LOTM.getInstance().saveConfig();

        System.out.println(config);
    }

    public static int getNPCSpawnRate() {
        return config.getInt("npc_spawn_rate");
    }
}
