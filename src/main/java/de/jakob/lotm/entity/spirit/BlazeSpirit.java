package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.EtherealFlame;
import de.jakob.lotm.pathways.abilities.other_abilities.ShadowShard;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

public class BlazeSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        return spawnSpirit(
                location,
                EntityType.BLAZE,
                false,
                true,
                36,
                1,
                6,
                new Particle.DustOptions[]{new Particle.DustOptions(Color.fromRGB(220, 68, 9), 1f)},
                new EtherealFlame(null, 6, AbilityType.SEQUENCE_PROGRESSION, "Ethereal Flame", Material.LIGHT_BLUE_CANDLE, "", "ethereal_flame")
        );
    }

}
