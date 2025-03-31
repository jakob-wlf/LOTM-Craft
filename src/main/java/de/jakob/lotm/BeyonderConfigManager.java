package de.jakob.lotm;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class BeyonderConfigManager {



    public static void loadBeyonders() {
        File file = new File(LOTM.getInstance().getDataFolder(), "beyonders.yml");

        if(!file.exists()) {
            if(file.getParentFile().mkdirs())
                LOTM.getInstance().saveResource("beyonders.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        LOTM.getLogUtil().info("Loading Beyonders");

        for(String uuid : config.getKeys(false)) {
            LOTM.getLogUtil().info("Loading Beyonder: " + uuid);

            String pathwayName = config.getString(uuid + ".pathway");
            int sequence = config.getInt(uuid + ".sequence");

            Beyonder beyonder = LOTM.getInstance().createBeyonder(
                    UUID.fromString(uuid),
                    LOTM.getInstance().getPathway(pathwayName),
                    sequence,
                    false,
                    false
            );

            if(beyonder == null)
                continue;

            ConfigurationSection abilitySection = config.getConfigurationSection(uuid + ".abilities");

            if(abilitySection == null)
                continue;

            LOTM.getLogUtil().info("Loading Abilities for Beyonder: " + uuid);

            for(String abilitiesPathway : abilitySection.getKeys(false)) {
                Pathway pathway = LOTM.getInstance().getPathway(abilitiesPathway);

                if(pathway == null)
                    continue;

                List<String> abilities = abilitySection.getStringList(abilitiesPathway);

                pathway.addAbilities(beyonder, abilities);

            }
        }
    }

    public static void saveBeyonders() {
        File file = new File(LOTM.getInstance().getDataFolder(), "beyonders.yml");

        if(file.delete())
            LOTM.getLogUtil().info("Deleted old Beyonders file");

        if(!file.exists()) {
            if(file.getParentFile().mkdirs())
                LOTM.getInstance().saveResource("beyonders.yml", false);
        }

        LOTM.getLogUtil().info("Saving Beyonders");

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        HashMap<UUID, Beyonder> beyonders = LOTM.getInstance().getBeyonders();

        for(Map.Entry<UUID, Beyonder> entry : beyonders.entrySet()) {
            if(!(entry.getValue() instanceof BeyonderPlayer))
                continue;

            LOTM.getLogUtil().info("Saving Beyonder: " + entry.getKey().toString());

            String uuid = entry.getKey().toString();
            Beyonder beyonder = entry.getValue();

            if(beyonder.getCurrentPathway() == null)
                continue;

            config.set(uuid + ".pathway", beyonder.getCurrentPathway().getName());
            config.set(uuid + ".sequence", beyonder.getCurrentSequence());

            LOTM.getLogUtil().info("Saving Abilities for Beyonder: " + entry.getKey().toString());

            HashMap<String, List<String>> abilities = new HashMap<>();
            for(Ability ability : beyonder.getAbilities()) {
                if(ability.getAbilityType() != AbilityType.SEQUENCE_PROGRESSION)
                    continue;
                if(abilities.containsKey(ability.getPathway().getName())) {
                    abilities.get(ability.getPathway().getName()).add(ability.getId());
                } else {
                    abilities.put(ability.getPathway().getName(), new ArrayList<>(List.of(ability.getId())));
                }
            }

            for(Map.Entry<String, List<String>> abilityEntry : abilities.entrySet()) {
                config.set(uuid + ".abilities." + abilityEntry.getKey(), abilityEntry.getValue());
            }
        }

        try {
            config.save(file);
        } catch (Exception e) {
            LOTM.getLogUtil().error("Error saving Beyonders: " + e.getMessage());
        }
    }
}
