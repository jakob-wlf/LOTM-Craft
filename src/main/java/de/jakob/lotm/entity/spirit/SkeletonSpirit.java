package de.jakob.lotm.entity.spirit;

import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.BoneSmash;
import de.jakob.lotm.pathways.beyonder.BeyonderSpirit;
import de.jakob.lotm.pathways.impl.death.impl.PhysicalEnhancements;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SkeletonSpirit extends Spirit{

    public static BeyonderSpirit spawn(Location location) {
        BeyonderSpirit spirit = spawnSpirit(
                location,
                EntityType.SKELETON,
                true,
                true,
                5,
                1.85,
                6,
                new Particle.DustOptions[]{new Particle.DustOptions(Color.fromRGB(180, 180, 180), 10f)},
                new PhysicalEnhancements(null, 9, AbilityType.SEQUENCE_PROGRESSION, "Physical Enhancements", Material.IRON_CHESTPLATE, "", "physical_enhancements_death"),
                new BoneSmash(null, 6, AbilityType.SEQUENCE_PROGRESSION, "Bone Smash", Material.BONE, "", "physical_enhancements_death")
        );

        if(spirit != null) {
            spirit.getEntity().getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            spirit.getEntity().getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            spirit.setDustAmount(12);
        }

        return spirit;
    }

}
