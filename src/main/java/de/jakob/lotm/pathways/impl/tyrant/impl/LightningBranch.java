package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.pathways.TyrantUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class LightningBranch extends Ability {

    public LightningBranch(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    //TODO: Sounds
    @Override
    public void useAbility(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(150))
            return;

        LivingEntity entity = beyonder.getEntity();
        Vector direction = getDirectionNormalized(entity, 35).multiply(.3);
        HashMap<Location, Vector> branches = new HashMap<>(Map.of(entity.getEyeLocation().add(direction), direction.clone()));
        World world = entity.getWorld();

        for(int i = 0; i < 85; i++) {
            if(random.nextInt(3) == 0 && i > 5) {
                branches.put(((Location) branches.keySet().toArray()[random.nextInt(branches.size())]).clone(), direction.clone());
            }

            for(Map.Entry<Location, Vector> entry : branches.entrySet()) {
                ParticleSpawner.displayParticles(world, Particle.SOUL_FIRE_FLAME, entry.getKey(), 1, 0, 0, 0, 0, TyrantUtil.blueDust2, 200);
                damageNearbyEntities(39, beyonder.getCurrentMultiplier(), entity, 1.4, entry.getKey(), world);
                if(i > 5)
                    entry.getValue().add(new Vector(random.nextDouble(-.075, .075), random.nextDouble(-.075, .075), random.nextDouble(-.075, .075))).normalize().multiply(.3);
                entry.getKey().add(entry.getValue());
            }
        }
    }


}
