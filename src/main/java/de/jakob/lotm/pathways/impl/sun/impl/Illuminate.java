package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class Illuminate extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), .8f);

    public Illuminate(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(20))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = getLocationLookedAt(entity, 4.5, true);

        entity.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, .5f, .8f);

        loc.getBlock().setType(Material.LIGHT);
        LOTM.getInstance().getBlocksToRemove().add(loc);

        runTaskWithDuration(2, 20 * 9, () -> {
            ParticleSpawner.displayParticles(entity.getWorld(), Particle.END_ROD, loc, 3, .2, .2, .2, 0, 120);
            ParticleUtil.drawCircle(loc, new Vector(0, 1, 0), .75, Particle.DUST, dust, 15);
            ParticleUtil.drawCircle(loc, new Vector(.05, 0, 1), .75, Particle.DUST, dust, 15);
            ParticleUtil.drawCircle(loc, new Vector(1, 0, 0), .75, Particle.DUST, dust, 15);
        }, () -> {
            LOTM.getInstance().getBlocksToRemove().remove(loc);
            loc.getBlock().setType(Material.AIR);
        });
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return false;
    }
}
