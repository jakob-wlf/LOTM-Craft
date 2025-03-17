package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.SpiritBolt;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

public class VexSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        return spawnSpirit(
                location,
                EntityType.VEX,
                false,
                true,
                15,
                1.7,
                8,
                new Particle.DustOptions[]{new Particle.DustOptions(Color.fromRGB(110, 34, 4), 1f)},
                new SpiritBolt(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Spirit Bolt", Material.LAPIS_LAZULI, "", "spirit_bolt")
        );
    }

}
