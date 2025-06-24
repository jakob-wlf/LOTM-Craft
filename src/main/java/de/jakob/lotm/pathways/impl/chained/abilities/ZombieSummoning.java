package de.jakob.lotm.pathways.impl.chained.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.other_abilities.ShadowShard;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class ZombieSummoning extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public ZombieSummoning(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        if(!beyonder.removeSpirituality(48))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        World world = entity.getWorld();
        Location loc = entity.getLocation();

        if(entity instanceof Player player)
            player.playSound(player, Sound.ENTITY_WITHER_SPAWN, .7f, .6f);

        for(int i = 0; i < 18; i++) {
            Location entityLoc = loc.clone().add(random.nextDouble(-9, 9), 0, random.nextDouble(-9, 9));
            while(entityLoc.getBlock().getType().isSolid())
                entityLoc.add(0, .5, 0);

            int breakoutCounter = 100;
            while (breakoutCounter > 0 && !entityLoc.getBlock().getRelative(0, -1, 0).getType().isSolid()) {
                entityLoc.subtract(0, .5, 0);
                breakoutCounter--;
            }

            EntityType entityType = EntityType.ZOMBIE;
            Zombie zombie = (Zombie) world.spawnEntity(entityLoc.clone().subtract(0, 1, 0), entityType);
            zombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));

            zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(60);
            zombie.setAI(false);
            zombie.getScoreboardTags().add("belongs_to_" + beyonder.getUuid());
            zombie.getScoreboardTags().add("no_spawn");
            zombie.getScoreboardTags().add("no_drop");
            Pathway chainedPathway = LOTM.getInstance().getPathway("chained");
            BeyonderEntity undeadBeyonder = (BeyonderEntity) LOTM.getInstance().createBeyonder(zombie.getUniqueId(), chainedPathway, Math.max(beyonder.getCurrentSequence(), 4), false, false, false);

            if(undeadBeyonder == null)
                return;

            undeadBeyonder.addAbility(chainedPathway.getAbility("physical_enhancements_chained"));
            if(random.nextInt(9) == 0)
                undeadBeyonder.addAbility(new ShadowShard(LOTM.getInstance().getPathway("chained"), 6, AbilityType.SEQUENCE_PROGRESSION, "Shadow Shard", Material.COAL, "", "shadow_shard"));
            if(random.nextInt(9) == 0)
                undeadBeyonder.addAbility(new WitherExplosion(chainedPathway, 6, AbilityType.SEQUENCE_PROGRESSION, "Wither Explosion", Material.SOUL_SOIL, "Create a powerful explosion that damages and knocks back nearby entities.", "wither_explosion"));
            if(random.nextInt(9) == 0)
                undeadBeyonder.addAbility(new ZombieTransformation(chainedPathway, 6, AbilityType.SEQUENCE_PROGRESSION, "Zombie Transformation", Material.ROTTEN_FLESH, "Transform into a zombie, gaining enhanced abilities and strength.", "zombie_transformation"));
            undeadBeyonder.setDefaultAI(true);
            undeadBeyonder.setDropsCharacteristic(false);

            runTaskWithDuration(2, 30, () -> {
                zombie.teleport(zombie.getLocation().clone().add(0, .1, 0));
                ParticleSpawner.displayParticles(world, Particle.SOUL, entityLoc, 8, 1, 0, 1, 0, 200);
            }, () -> zombie.setAI(true));

            Bukkit.getScheduler().runTaskLater(plugin, undeadBeyonder::destroyBeyonder, 20 * 25);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                casting.remove(beyonder);
                ParticleSpawner.displayParticles(world, Particle.SOUL_FIRE_FLAME, zombie.getLocation().clone().add(0, .6, 0), 15, .1, .3, .1, 0, 200);
                ParticleSpawner.displayParticles(world, Particle.SMOKE, zombie.getLocation().clone().add(0, .6, 0), 35, .1, .3, .1, 0, 200);
            }, 20 * 25);
        }
    }
}
