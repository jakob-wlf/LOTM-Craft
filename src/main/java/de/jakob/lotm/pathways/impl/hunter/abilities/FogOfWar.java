package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class FogOfWar extends ToggleableAbility {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(220, 220, 250), 8f);

    public FogOfWar(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        tickDelay = 5;
        spiritualityCost = 175;
    }

    @Override
    protected void impl(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getEyeLocation();

        World world = loc.getWorld();
        if(world == null)
            return;

        ParticleSpawner.displayParticles(world, Particle.DUST, loc, 750, 14, 4, 14, 0, dust, 200);

        addPotionEffectToNearbyEntities(entity, 20, loc, world, new PotionEffect(PotionEffectType.BLINDNESS, 20, 20, false, false, false), new PotionEffect(PotionEffectType.WEAKNESS, 20, 4, false, false, false), new PotionEffect(PotionEffectType.SLOWNESS, 20, 2, false, false, false));
        getNearbyLivingEntities(entity, 20, loc, world, true).forEach(e -> {
            Beyonder targetBeyonder = LOTM.getInstance().getBeyonder(e.getUniqueId());
            if(targetBeyonder != null) {
                targetBeyonder.addMultiplierModifier(.5, 5);
            }
        });

    }
}
