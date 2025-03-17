package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.LifeTree;
import de.jakob.lotm.pathways.abilities.other_abilities.Restraint;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

public class CowSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        return spawnSpirit(
                location,
                EntityType.MOOSHROOM,
                false,
                true,
                18,
                1,
                6,
                new Particle.DustOptions[]{new Particle.DustOptions(Color.fromRGB(103, 214, 140), 1f)},
                new LifeTree(null, 6, AbilityType.SEQUENCE_PROGRESSION, "Life Tree", Material.OAK_SAPLING, "", "life_tree"),
                new Restraint(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Restraint", Material.LEAD, "", "restraint")
        );
    }

}
