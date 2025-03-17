package de.jakob.lotm.pathways.impl.moon.impl.potions;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ShrinkingPotion extends MysticalPotion{

    public ShrinkingPotion(String name, String colorPrefix, Color color) {
        super(name, colorPrefix, color);
    }

    @Override
    public void onDrink(LivingEntity entity) {
        AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
        if(attribute != null) {
            if(attribute.getBaseValue() != 1) {
                return;
            }
            attribute.setBaseValue(.1);
        }

        if(entity instanceof Player) {
            ((Player) entity).playSound(entity.getLocation(), Sound.ENTITY_WITCH_DRINK, 1, 1);
            ((Player) entity).playSound(entity.getLocation(), Sound.BLOCK_BEEHIVE_EXIT, 1, 1);
        }
        ParticleSpawner.displayParticles(entity.getWorld(), Particle.DUST, entity.getLocation(), 100, .5, .5, .5, 0, dustOptions, 100);
        Beyonder beyonder = LOTM.getInstance().getBeyonder(entity.getUniqueId());
        if(beyonder != null) {
            LOTM.getInstance().getResetScale().add(beyonder);
        }

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            if(attribute != null) {
                attribute.setBaseValue(1);
            }

            if(beyonder != null) {
                LOTM.getInstance().getResetScale().remove(beyonder);
            }
        }, 20 * 60);


    }
}
