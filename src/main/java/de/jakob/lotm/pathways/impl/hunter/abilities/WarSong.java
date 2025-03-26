package de.jakob.lotm.pathways.impl.hunter.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.EntityUtil;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class WarSong extends Ability {

    public WarSong(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        spirituality = 200;
        cooldownTicks = 20 * 20;
        hasCooldown = true;

    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getEyeLocation();
        World world = entity.getWorld();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld() != world || player.getLocation().distance(loc) > 20)
                continue;
            player.playSound(loc, Sound.MUSIC_DISC_STAL, 1, 1);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.stopSound(Sound.MUSIC_DISC_STAL), 20 * 5);
        }

        getNearbyLivingEntities(entity, 25, loc, world, true).forEach(e -> {
            e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 20, 4, false, false, false));
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 2, false, false, false));
            Beyonder targetBeyonder = LOTM.getInstance().getBeyonder(e.getUniqueId());
            if(targetBeyonder != null) {
                targetBeyonder.addMultiplierModifier(.7, 20 * 20);
            }
        });
        getNearbyLivingEntities(entity, 25, loc, world).stream().filter(e -> EntityUtil.areOnTheSameTeam(entity, e)).forEach(e -> {
            e.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 4, false, false, false));
            e.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 6, false, false, false));
            e.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 20, 3, false, false, false));
            Beyonder targetBeyonder = LOTM.getInstance().getBeyonder(e.getUniqueId());
            if(targetBeyonder != null) {
                targetBeyonder.addMultiplierModifier(1.3, 20 * 20);
            }
        });
    }
}
