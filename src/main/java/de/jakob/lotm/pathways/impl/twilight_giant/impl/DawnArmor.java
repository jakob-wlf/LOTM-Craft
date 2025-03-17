package de.jakob.lotm.pathways.impl.twilight_giant.impl;

import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.Pathway;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class DawnArmor extends Ability {

    private final ArrayList<Beyonder> currentlyCasting = new ArrayList<>();
    private final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(237, 231, 213), 1.4f);
    private final Particle.DustOptions dustOptions2 = new Particle.DustOptions(Color.fromRGB(255, 214, 92), 1.4f);

    private final PotionEffect potionEffect = new PotionEffect(PotionEffectType.HEALTH_BOOST, 60, 4, false, false, false);
    private final PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.RESISTANCE, 60, 3, false, false, false);

    public DawnArmor(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
        canBeCopied = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(currentlyCasting.contains(beyonder)) {
            currentlyCasting.remove(beyonder);
            return;
        }

        beyonder.getEntity().getWorld().playSound(beyonder.getEntity().getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);

        currentlyCasting.add(beyonder);

        new BukkitRunnable() {

            int counter = 20;

            @Override
            public void run() {
                if(beyonder.getEntity() == null || !beyonder.getEntity().isValid()) {
                    currentlyCasting.remove(beyonder);
                    cancel();
                    return;
                }
                if(!currentlyCasting.contains(beyonder) || (counter == 20 && !beyonder.removeSpirituality(5))) {
                    beyonder.getEntity().getWorld().playSound(beyonder.getEntity().getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
                    currentlyCasting.remove(beyonder);
                    cancel();
                    return;
                }

                beyonder.getEntity().getWorld().spawnParticle(Particle.DUST, beyonder.getEntity().getLocation().add(0, .55, 0), 5, .2, .5, .2, 0, dustOptions);
                beyonder.getEntity().getWorld().spawnParticle(Particle.DUST, beyonder.getEntity().getLocation().add(0, .55, 0), 5, .2, .5, .2, 0, dustOptions2);

                if(counter == 20) {
                    counter = 0;
                    beyonder.getEntity().addPotionEffect(potionEffect);
                    beyonder.getEntity().addPotionEffect(potionEffect2);
                }

                counter+=2;
            }
        }.runTaskTimer(plugin, 0, 2);
    }
}
