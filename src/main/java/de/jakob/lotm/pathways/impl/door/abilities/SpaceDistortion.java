package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Marker;

@NoArgsConstructor
public class SpaceDistortion extends Ability {

    private static final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(74, 24, 125), 2f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(120, 208, 245), 2f);

    public SpaceDistortion(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20 * 8;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();

        Location targetLoc = getTargetLocation(entity, 16).add(0, 1, 0);
        World world = targetLoc.getWorld();
        if(world == null)
            return;

        Marker marker = (Marker) world.spawnEntity(targetLoc, EntityType.MARKER);
        marker.getScoreboardTags().add("distortion_pull");
        marker.getScoreboardTags().add("user_" + entity.getUniqueId());

        runTaskWithDuration(5, 20 * 20, () -> {
            world.playSound(targetLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, .65f, 1);
            world.playSound(targetLoc, Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1);
            world.playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, .35f, .1f);

            ParticleSpawner.displayParticles(Particle.PORTAL, targetLoc, 350, 1, 1, 1, .45, 200);
            ParticleSpawner.displayParticles(Particle.END_ROD, targetLoc, 20, .5, .5, .5, 0, 200);
            ParticleSpawner.displayParticles(Particle.DUST, targetLoc, 30, .5, .5, .5, 0, dust, 200);
            ParticleSpawner.displayParticles(Particle.DUST, targetLoc, 30, .5, .5, .5, 0, dust2, 200);
            getNearbyLivingEntities(entity, 2, targetLoc, world, true).forEach(e -> {
                e.teleport(targetLoc.clone().add(random.nextDouble() * 12 - 6, random.nextDouble() * 5, random.nextDouble() * 12 - 6));
            });
        }, marker::remove);
    }
}
