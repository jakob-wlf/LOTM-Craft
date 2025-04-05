package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class Seal extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(224, 120, 245), 2f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(120, 208, 245), 2f);

    public Seal(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20;
        spirituality = 300;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = getTargetEntity(entity, 16);

        if (target == null) {
            if(entity instanceof Player player)
                player.sendMessage("§dNo target found!");
            return;
        }

        Beyonder beyonderTarget = plugin.getBeyonder(target.getUniqueId());

        if(beyonderTarget == null || beyonderTarget.getCurrentSequence() > beyonder.getCurrentSequence()) {
            ParticleUtil.createParticleSphere(target.getLocation().add(0, .6, 0), 1, 20, Particle.DUST, dust);
            ParticleUtil.createParticleSphere(target.getLocation().add(0, .6, 0), 1, 20, Particle.DUST, dust2);
            ParticleUtil.createParticleSphere(target.getLocation().add(0, .6, 0), 1, 20, Particle.END_ROD);

            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, .15f, 1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            System.out.println("0");

            Location endLocation = createLocationInEnd(random.nextInt(347246, 347646), 200, random.nextInt(-323223, -323623));

            System.out.println("1");

            if(endLocation == null) {
                return;
            }

            System.out.println("2");

            BlockUtil.getBlocksInRectangle(endLocation.clone().add(3, -1, 3), endLocation.clone().add(-3, -1, -3), false).forEach(b -> {
                if(!b.getType().isSolid())
                    b.setType(Material.BARRIER);
            });

            target.teleport(endLocation);
            target.setNoDamageTicks(0);
            target.setFallDistance(0);
            target.setVelocity(new Vector(0, 0, 0));
            target.setFireTicks(0);
            target.setGlowing(false);
            target.setInvisible(false);
            target.setInvulnerable(false);
            target.setSilent(false);
            return;
        }

        if(beyonderTarget.getCurrentSequence() < beyonder.getCurrentSequence() - 1)
            return;

        if(beyonderTarget.getEntity() == null || beyonderTarget.getEntity().getScoreboardTags().contains("is_sealed"))
            return;

        beyonderTarget.getEntity().addScoreboardTag("is_sealed");

        int duration = beyonder.getCurrentSequence() == beyonderTarget.getCurrentSequence() ? 20 * 12 : 20 * 3;

        runTaskWithDuration(4, duration, () -> {
            ParticleUtil.createParticleSphere(beyonderTarget.getEntity().getLocation().add(0, .6, 0), 2, 20, Particle.DUST, dust);
            ParticleUtil.createParticleSphere(beyonderTarget.getEntity().getLocation().add(0, .6, 0), 2, 20, Particle.DUST, dust2);
            ParticleUtil.createParticleSphere(beyonderTarget.getEntity().getLocation().add(0, .6, 0), 2, 20, Particle.END_ROD);

            beyonderTarget.getEntity().getWorld().playSound(beyonderTarget.getEntity().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, .5f, 1);
            beyonderTarget.disablePowers(6);

            beyonderTarget.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 10, false, false, false));
            restrictMovement(beyonderTarget.getEntity());

        }, () -> Bukkit.getScheduler().runTaskLater(plugin, () -> beyonderTarget.getEntity().getScoreboardTags().remove("is_sealed"), 20 * 4));
    }

    private Location createLocationInEnd(double x, double y, double z) {
        // Attempt to get the End world
        World endWorld = Bukkit.getWorld("world_the_end");

        // If the world is not loaded, load it
        if (endWorld == null) {
            endWorld = Bukkit.createWorld(new WorldCreator("world_the_end"));
        }

        // If the world is successfully loaded, create a Location
        if (endWorld != null) {
            return new Location(endWorld, x, y, z);
        } else {
            return null;
        }
    }
}
