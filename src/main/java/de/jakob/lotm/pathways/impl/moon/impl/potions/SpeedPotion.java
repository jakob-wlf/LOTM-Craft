package de.jakob.lotm.pathways.impl.moon.impl.potions;

import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedPotion extends MysticalPotion{
    public SpeedPotion(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onDrink(LivingEntity entity) {
        if(entity instanceof Player) {
            ((Player) entity).playSound(entity.getLocation(), Sound.ENTITY_WITCH_DRINK, 1, 1);
            ((Player) entity).playSound(entity.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1, 1);
        }
        ParticleSpawner.displayParticles(entity.getWorld(), Particle.DUST, entity.getLocation(), 100, .5, .5, .5, 0, dustOptions, 100);

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 10, false, false));
    }
}
