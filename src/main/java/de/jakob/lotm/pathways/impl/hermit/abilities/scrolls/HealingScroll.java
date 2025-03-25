package de.jakob.lotm.pathways.impl.hermit.abilities.scrolls;

import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class HealingScroll extends MysticalScroll{
    public HealingScroll(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onUse(LivingEntity entity) {
        Location loc = entity.getEyeLocation();

        World world = loc.getWorld();
        if(world == null)
            return;

        world.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);

        runTaskWithDuration(4, 20 * 5, () -> {
            world.getNearbyEntities(loc, 6, 5, 6).stream().filter(e -> (e == entity || EntityUtil.areOnTheSameTeam(e, entity)) && e instanceof LivingEntity).map(e -> (LivingEntity) e).forEach(e -> e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 8, false, false, false)));
            ParticleUtil.drawCircle(loc.clone().add(0, 2.5, 0), new Vector(0, 1, 0), 2, Particle.DUST, dustOptions, 50);
        }, null);
    }
}
