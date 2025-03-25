package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import de.jakob.lotm.util.minecraft.VectorUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

@NoArgsConstructor
public class DeadFlame extends ToggleableAbility {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(255, 173, 115), 1f);


    public DeadFlame(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        tickDelay = 2;
        constantSpiritualityCost = 10;

    }

    @Override
    protected void impl(Beyonder beyonder) {

        Vector direction = beyonder.getEntity().getEyeLocation().getDirection().normalize();

        for(double j = -.5; j < 1; j++) {
            for(double i = -1; i < 1.25; i+=.25) {
                Vector vector = VectorUtil.rotateAroundY(direction, 45 * i);

                ParticleUtil.drawLine(beyonder.getEntity().getEyeLocation().add(direction.clone().multiply(2)).add(0, j, 0), vector, Particle.DUST, 1, beyonder.getEntity().getWorld(), dust, 3, .4, 25);
                ParticleUtil.drawLine(beyonder.getEntity().getEyeLocation().add(direction.clone().multiply(2)).add(0, j, 0), vector, Particle.DUST, 1, beyonder.getEntity().getWorld(), dust2, 1, .4, 25);
                if(random.nextInt(4) == 0) {
                    ParticleUtil.drawLine(beyonder.getEntity().getEyeLocation().add(direction.clone().multiply(2)).add(0, j, 0), vector, Particle.END_ROD, 2.5, beyonder.getEntity().getWorld(), null, 1, .5, 25);
                    ParticleUtil.drawLine(beyonder.getEntity().getEyeLocation().add(direction.clone().multiply(2)).add(0, j, 0), vector, Particle.FLAME, 2.5, beyonder.getEntity().getWorld(), null, 1, .5, 25);
                }

                damageInDirection(60, beyonder.getCurrentMultiplier(), beyonder.getEntity(), 25, 1.4, beyonder.getEntity().getEyeLocation().add(direction.clone().multiply(2)), beyonder.getEntity().getWorld(), false, 0, 10, false, vector);
            }
        }

    }
}
