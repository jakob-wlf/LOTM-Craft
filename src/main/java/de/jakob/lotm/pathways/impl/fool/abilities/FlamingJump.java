package de.jakob.lotm.pathways.impl.fool.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class FlamingJump extends Ability {

    public FlamingJump(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        spirituality = 15;
        canBeUsedByNonPlayer = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        Block targetBlock = getTargetBlockOfType(beyonder.getEntity(), 45, Material.FIRE);
        if(targetBlock == null)
            return;

        beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 4, 0, false, false, false));
        Location teleportLocation = targetBlock.getLocation().add(0.5, 1, 0.5);
        teleportLocation.setPitch(beyonder.getEntity().getLocation().getPitch());
        teleportLocation.setYaw(beyonder.getEntity().getLocation().getYaw());
        beyonder.getEntity().teleport(teleportLocation);
        beyonder.getEntity().getWorld().playSound(beyonder.getEntity().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
        beyonder.getEntity().getWorld().spawnParticle(Particle.FLAME, beyonder.getEntity().getEyeLocation().subtract(0, .4, 0), 80, 0.2, 0.7, 0.2, 0);
        beyonder.getEntity().getWorld().spawnParticle(Particle.SMOKE, beyonder.getEntity().getEyeLocation().subtract(0, .4, 0), 80, 0.2, 0.7, 0.2, 0);

        runTaskWithDuration(2, 20 * 4, () -> beyonder.getEntity().setFireTicks(0));
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        Block targetBlock = getTargetBlockOfType(player, 45, Material.FIRE);
        if(targetBlock == null)
            return;
        player.spawnParticle(Particle.FLASH, targetBlock.getLocation().add(0.5, 0.5, 0.5), 1);
    }
}
