package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.SpiritAbilities;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;

public class DefaultSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        List<Ability> abilities = SpiritAbilities.abilities;

        return spawnSpirit(location, EntityType.ALLAY, false, false, 20, 1, 8, null, abilities.get(random.nextInt(abilities.size())));
    }

}
