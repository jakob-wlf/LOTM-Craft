package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PhysicalEnhancements extends PassiveAbility {

    private final HashMap<Integer, PotionEffect[]> effectsPerSequence;

    public PhysicalEnhancements(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        effectsPerSequence = new HashMap<>(Map.of(
                9, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 0),
                        new PotionEffect(PotionEffectType.SPEED, 0, 0),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 0),
                },
                7, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 1),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 0),
                        new PotionEffect(PotionEffectType.SPEED, 0, 2),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 5),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 2),
                },
                6, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 1),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 1),
                        new PotionEffect(PotionEffectType.SPEED, 0, 2),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 6),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 1),
                },
                5, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 1),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 1),
                        new PotionEffect(PotionEffectType.SPEED, 0, 2),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 7),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 1),
                },
                4, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 2),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 2),
                        new PotionEffect(PotionEffectType.SPEED, 0, 3),
                        new PotionEffect(PotionEffectType.SATURATION, 0, 2),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 11),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 2),
                },
                3, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 2),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 2),
                        new PotionEffect(PotionEffectType.SPEED, 0, 3),
                        new PotionEffect(PotionEffectType.SATURATION, 0, 2),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 14),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 2),
                },
                2, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 3),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 3),
                        new PotionEffect(PotionEffectType.SPEED, 0, 3),
                        new PotionEffect(PotionEffectType.SATURATION, 0, 5),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 18),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 3),
                },
                1, new PotionEffect[]{
                        new PotionEffect(PotionEffectType.STRENGTH, 0, 3),
                        new PotionEffect(PotionEffectType.RESISTANCE, 0, 3),
                        new PotionEffect(PotionEffectType.SPEED, 0, 3),
                        new PotionEffect(PotionEffectType.SATURATION, 0, 5),
                        new PotionEffect(PotionEffectType.HEALTH_BOOST, 0, 22),
                        new PotionEffect(PotionEffectType.REGENERATION, 0, 3),
                }
        ));
    }

    @Override
    public PotionEffect[] getPotionEffect(Beyonder beyonder) {
        int sequence = beyonder.getCurrentSequence();
        for(int i = sequence; i < 10; i++) {
            PotionEffect[] effects = effectsPerSequence.get(i);
            if(effects != null) {
                return effects;
            }
        }

        return null;
    }
}
