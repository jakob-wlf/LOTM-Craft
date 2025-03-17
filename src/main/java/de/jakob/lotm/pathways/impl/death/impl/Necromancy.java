package de.jakob.lotm.pathways.impl.death.impl;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class Necromancy extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public Necromancy(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
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

            EntityType entityType = switch (random.nextInt(4)) {
                case 1 -> EntityType.HUSK;
                case 2 -> EntityType.VEX;
                case 3 -> EntityType.WITHER_SKELETON;
                default -> EntityType.SKELETON;
            };
            LivingEntity undead = (LivingEntity) world.spawnEntity(entityLoc.clone().subtract(0, 1, 0), entityType);
            if(undead instanceof Skeleton skeleton) {
                skeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
            }

            undead.getAttribute(Attribute.MAX_HEALTH).setBaseValue(60);
            undead.setAI(false);
            undead.getScoreboardTags().add("belongs_to_" + beyonder.getUuid());
            undead.getScoreboardTags().add("no_spawn");
            undead.getScoreboardTags().add("no_drop");
            Pathway deathPathway = LOTM.getInstance().getPathway("death");
            BeyonderEntity undeadBeyonder = (BeyonderEntity) LOTM.getInstance().createBeyonder(undead.getUniqueId(), deathPathway, beyonder.getCurrentSequence(), false, false, false);
            if(undeadBeyonder == null)
                return;
            undeadBeyonder.addAbility(deathPathway.getAbility("physical_enhancements_death"));
            if(random.nextInt(7) == 0)
                undeadBeyonder.addAbility(new ShadowShard(LOTM.getInstance().getPathway("Death"), 6, AbilityType.SEQUENCE_PROGRESSION, "Shadow Shard", Material.COAL, "", "shadow_shard"));
            undeadBeyonder.setDefaultAI(true);
            undeadBeyonder.setDropsCharacteristic(false);

            runTaskWithDuration(2, 30, () -> {
                undead.teleport(undead.getLocation().clone().add(0, .1, 0));
                ParticleSpawner.displayParticles(world, Particle.SOUL, entityLoc, 8, 1, 0, 1, 0, 200);
            }, () -> undead.setAI(true));

            Bukkit.getScheduler().runTaskLater(plugin, undeadBeyonder::destroyBeyonder, 20 * 25);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                casting.remove(beyonder);
                ParticleSpawner.displayParticles(world, Particle.SOUL_FIRE_FLAME, undead.getLocation().clone().add(0, .6, 0), 15, .1, .3, .1, 0, 200);
                ParticleSpawner.displayParticles(world, Particle.SMOKE, undead.getLocation().clone().add(0, .6, 0), 35, .1, .3, .1, 0, 200);
            }, 20 * 25);


        }
    }
}
