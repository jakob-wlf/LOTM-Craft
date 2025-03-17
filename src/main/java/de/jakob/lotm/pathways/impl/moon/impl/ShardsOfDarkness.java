package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

@NoArgsConstructor
public class ShardsOfDarkness extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(20, 20, 20), 1);

    public ShardsOfDarkness(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(20))
            return;
        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location targetLoc = getTargetLocation(beyonder.getEntity(), 35);
        Location startLoc = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().normalize().multiply(1.5));

        for(int i = 0; i < 3; i++) {
            Location loc = startLoc.clone().add(random.nextDouble(-3.5, 3.5), random.nextDouble(-1, 2.5), random.nextDouble(-3.5, 3.5));
            Vector direction = targetLoc.toVector().subtract(loc.toVector()).normalize();
            ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
            itemDisplay.setItemStack(new ItemStack(Material.BLACK_CONCRETE));
            Transformation transformation = itemDisplay.getTransformation();
            transformation.getScale().set(.55);
            transformation.getLeftRotation().set((new Quaternionf(random.nextFloat(-1, 1), random.nextFloat(-1, 1), random.nextFloat(-1, 1), random.nextFloat() * Math.PI * 2)).normalize());
            itemDisplay.setTransformation(transformation);
            itemDisplay.setGravity(false);
            LOTM.getInstance().getEntitiesToRemove().add(itemDisplay);
            playSound(beyonder, Sound.BLOCK_GLASS_BREAK, .4f, .1f);
            playSound(beyonder, Sound.ENTITY_ARROW_SHOOT, .4f, .1f);
            runTaskWithDuration(1, 20 * 3, () ->{
                if(!itemDisplay.isValid())
                    return;

                ParticleSpawner.displayParticles(world, Particle.DUST, loc, 45, .1, .1, .1, 0, dust, 200);
                itemDisplay.teleport(loc);
                damageNearbyEntities(12.5, beyonder.getCurrentMultiplier(), entity, 1.5, loc, world);
                loc.add(direction);

                if(loc.getBlock().getType().isSolid())
                    itemDisplay.remove();
            }, () -> {
                itemDisplay.remove();
                LOTM.getInstance().getEntitiesToRemove().remove(itemDisplay);
            });
        }
    }
}
