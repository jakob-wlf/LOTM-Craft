package de.jakob.lotm.pathways.impl.fool.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class UnderwaterBreathing extends ToggleableAbility {

    public UnderwaterBreathing(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void init() {
        canBeUsedByNonPlayer = false;
        constantSpiritualityCost = 1;
        tickDelay = 4;
    }

    @Override
    public void start(Beyonder beyonder) {
        if(beyonder.getEntity().getEyeLocation().getBlock().getType() != Material.WATER)
            casting.remove(beyonder);
    }

    @Override
    public void impl(Beyonder beyonder) {
        if(beyonder.getEntity().getEyeLocation().getBlock().getType() != Material.WATER) {
            casting.remove(beyonder);
            return;
        }

        LivingEntity entity = beyonder.getEntity();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20, 0, false, false, false));
        Location startLocation = entity.getEyeLocation().add(entity.getEyeLocation().getDirection().normalize().multiply(.6));
        while(startLocation.getBlock().getType() == Material.WATER) {
            startLocation.add(0, .5, 0);
            ParticleSpawner.displayParticles(Particle.BUBBLE, startLocation, 3, .05, .05, .05, 0, 80);
        }
    }
}
