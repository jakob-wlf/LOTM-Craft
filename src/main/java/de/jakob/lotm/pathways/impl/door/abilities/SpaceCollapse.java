package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class SpaceCollapse extends Ability {

    private static final Particle.DustOptions dust3 = new Particle.DustOptions(Color.fromRGB(121, 212, 237), .7f);

    private static final Particle.DustOptions dust12 = new Particle.DustOptions(Color.fromRGB(74, 24, 125), 2f);
    private static final Particle.DustOptions dust22 = new Particle.DustOptions(Color.fromRGB(5, 5, 10), 2f);
    private static final Particle.DustOptions dust32 = new Particle.DustOptions(Color.fromRGB(121, 212, 237), 2f);
    private static final BlockData block1 = Material.GLASS.createBlockData();

    public SpaceCollapse(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(400))
            return;

        LivingEntity entity = beyonder.getEntity();
        Location loc = getTargetLocation(entity, 16).add(0, 1, 0);
        World world = loc.getWorld();

        Vector direction = VectorUtil.rotateAroundY(beyonder.getEntity().getEyeLocation().getDirection().setY(0), 90);
        if(world == null)
            return;

        Vector ovalDir = VectorUtil.rotateAroundY(direction, -90);

        runTaskWithDuration(2, 10, () -> {
            ParticleUtil.displayOvalOutline(world, Particle.DUST, loc, 7, 5, 10, ovalDir, dust3, 1.5, 0);
        }, () -> runTaskWithDuration(8, 16, () -> {
            ParticleUtil.displayOvalOutline(world, Particle.DUST, loc, 7, 5, 10, ovalDir, dust12, 1, .1);
            ParticleUtil.displayOvalOutline(world, Particle.DUST, loc, 7, 5, 10, ovalDir, dust22, 1, .1);
            ParticleUtil.displayOvalOutline(world, Particle.DUST, loc, 7, 5, 10, ovalDir, dust32, .45, .1);
        }, () -> runTaskWithDuration(5, 20 * 35, () -> {
            ParticleUtil.displayOvalOutline(world, Particle.DUST, loc, 7, 5, 10, ovalDir, dust22, 1, .1);
        }, null)));

//        runTaskWithDuration(9, 20 * 60, () -> {
//            ParticleUtil.displayParticleRectangle(world, Particle.DUST, loc, 3, 9, direction, dust2, 2);
//            ParticleUtil.displayParticleRectangle(world, Particle.DUST, loc, 3, 9, direction, dust1, 2);
//        }, null);
    }
}
