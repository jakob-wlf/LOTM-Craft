package de.jakob.lotm.pathways.abilities;

import de.jakob.lotm.pathways.abilities.other_abilities.*;
import org.bukkit.Material;

import java.util.List;

public class SpiritAbilities {

    public static final List<Ability> abilities = List.of(
            new Restraint(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Restraint", Material.LEAD, "", "restraint"),
            new MagicTurret(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Magic Turret", Material.IRON_INGOT, "", "magic_turret"),
            new Dash(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Dash", Material.IRON_BOOTS, "", "dash"),
            new SpiritBlast(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Spirit Blast", Material.AMETHYST_SHARD, "", "spirit_blast"),
            new SpectralProjectiles(null, 8, AbilityType.SEQUENCE_PROGRESSION, "Spectral Projectiles", Material.GOLD_INGOT, "", "spectral_projectiles")
    );

}
