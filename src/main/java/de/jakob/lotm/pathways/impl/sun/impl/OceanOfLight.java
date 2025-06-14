package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
public class OceanOfLight extends Ability {

    private final HashSet<Beyonder> casting = new HashSet<>();
    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 5f);

    public OceanOfLight(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        canBeUsedByNonPlayer = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        if(!beyonder.removeSpirituality(500))
            return;

        casting.add(beyonder);

        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getLocation();
        if(loc.getWorld() == null)
            return;

        List<Location> lightBlocks = BlockUtil.getBlocksInCircleRadius(loc.getBlock(), 50, false).stream().filter(block -> block.getType() == Material.AIR && block.getRelative(0, -1, 0).getType().isSolid()).map(Block::getLocation).toList();
        lightBlocks.forEach(b -> b.getBlock().setType(Material.LIGHT));
        LOTM.getInstance().getBlocksToRemove().addAll(lightBlocks.stream().toList());

        runTaskWithDuration(12, 20 * 30, () -> {
            ParticleSpawner.displayParticles(loc.getWorld(), Particle.END_ROD, loc, 620, 19, 5, 19, 0, dust, 90);

            addPotionEffectToNearbyEntities(entity, 30, loc, loc.getWorld(), new PotionEffect(PotionEffectType.GLOWING, 30, 1, false, false, false));
            damageNearbyEntities(12, beyonder.getCurrentMultiplier(), entity, 30, loc, loc.getWorld(), false, 0, 0, true);
        }, () -> {
            casting.remove(beyonder);
            lightBlocks.forEach(b -> {
                LOTM.getInstance().getBlocksToRemove().remove(b);
                b.getBlock().setType(Material.AIR);
            });
        });
    }
}
