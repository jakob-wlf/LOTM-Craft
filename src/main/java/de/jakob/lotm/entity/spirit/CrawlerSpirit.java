package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.BoneSmash;
import de.jakob.lotm.pathways.abilities.other_abilities.Dash;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import de.jakob.lotm.pathways.impl.death.impl.PhysicalEnhancements;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

public class CrawlerSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        BeyonderSpirit spirit = spawnSpirit(
                location,
                EntityType.SPIDER,
                true,
                true,
                5,
                1.2,
                5,
                new Particle.DustOptions[]{new Particle.DustOptions(Color.fromRGB(50, 50, 50), 2.2f)},
                new PhysicalEnhancements(null, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "", "physical_enhancements_death"),
                new Dash(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Dash", Material.IRON_BOOTS, "", "dash")
        );
        return spirit;
    }
}
