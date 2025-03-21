package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

@NoArgsConstructor
public class FlaringSun extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2.2f);
    private final HashMap<Beyonder, Integer> casting = new HashMap<>();

    public FlaringSun(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.containsKey(beyonder) && casting.get(beyonder) >= 2)
            return;

        if(!beyonder.removeSpirituality(230))
            return;

        if(!casting.containsKey(beyonder))
            casting.put(beyonder, 1);
        else
            casting.replace(beyonder, casting.get(beyonder) + 1);

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 13.5);

        World world = loc.getWorld();
        if(world == null)
            return;

        if(!loc.getBlock().getType().isSolid()) {
            loc.getBlock().setType(Material.LIGHT);
            LOTM.getInstance().getBlocksToRemove().add(loc);
        }

        runTaskWithDuration(4, 20 * 12, () -> {
            if(random.nextInt(3) == 0)
                world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, .6f, .75f);

            ParticleUtil.createParticleSphere(loc, 3.5, 95, Particle.FLAME);
            ParticleUtil.createParticleSphere(loc, 3.5, 70, Particle.DUST, dust);

            damageNearbyEntities(31, beyonder.getCurrentMultiplier(), entity, 3.6, loc, world, true, 45);
        }, () -> {
            if(casting.containsKey(beyonder) && casting.get(beyonder) > 0)
                casting.replace(beyonder, casting.get(beyonder) - 1);

            if(loc.getBlock().getType() == Material.LIGHT) {
                loc.getBlock().setType(Material.AIR);
                LOTM.getInstance().getBlocksToRemove().remove(loc);
            }
        });

    }
}
