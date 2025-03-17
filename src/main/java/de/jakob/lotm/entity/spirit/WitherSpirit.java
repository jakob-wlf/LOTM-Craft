package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.ShadowShard;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

public class WitherSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        return spawnSpirit(
                location,
                EntityType.WITHER_SKELETON,
                true,
                true,
                15,
                1,
                7,
                new Particle.DustOptions[]{new Particle.DustOptions(Color.BLACK, 1f)},
                new ShadowShard(null, 7, AbilityType.SEQUENCE_PROGRESSION, "Shadow Shard", Material.COAL, "", "shadow_shard")
        );
    }

}
