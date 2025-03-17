package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;

@NoArgsConstructor
public class FlashTeleportation extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(232, 26, 63), 2.3f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(30, 30, 30), 1.4f);

    public FlashTeleportation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.getEntity().getScoreboardTags().contains("lunar") && beyonder.getEntity().getWorld().getTime() < 12000)
            return;

        if(!beyonder.removeSpirituality(20))
            return;

        Location targetLocation = getTargetBlock(beyonder.getEntity(), 12).getLocation();
        if(targetLocation.getBlock().getType().isSolid()) targetLocation.add(0, 1, 0);
        targetLocation.setDirection(beyonder.getEntity().getLocation().getDirection());

        beyonder.getEntity().teleport(targetLocation);

        World world = beyonder.getEntity().getWorld();

        ParticleSpawner.displayParticles(world, Particle.DUST, beyonder.getEntity().getLocation().add(0, .5, 0), 25, .3, .85, .3, 0, dust, 200);
        ParticleSpawner.displayParticles(world, Particle.DUST, beyonder.getEntity().getLocation().add(0, .5, 0), 25, .3, .85, .3, 0, dust2, 200);
        world.playSound(beyonder.getEntity().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, .8f, 1);
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return beyonder.getEntity().getScoreboardTags().contains("lunar");
    }
}
