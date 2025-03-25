package de.jakob.lotm.pathways.impl.death.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GazeOfDeath extends ToggleableAbility {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(102, 82, 60), 2);

    public GazeOfDeath(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    //TODO: Sounds
    @Override
    protected void impl(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(3)) {
            casting.remove(beyonder);
            return;
        }

        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = getTargetEntity(entity, 30, true, .65, false);

        if(target == null)
            return;

        Beyonder targetBeyonder = LOTM.getInstance().getBeyonder(target.getUniqueId());
        if((targetBeyonder == null || targetBeyonder.getCurrentSequence() >= 5) && target.getHealth() < 120) {
            target.damage(1000, entity);
        }
        else {
            target.damage(8 * beyonder.getCurrentMultiplier(), entity);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 10, false, false, false));
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 10, false, false, false));
        }

        ParticleSpawner.displayParticles(target.getWorld(), Particle.SOUL, target.getEyeLocation().subtract(0, target.getHeight() / 2, 0), 4, .3, target.getHeight() / 2, .3, 0, 200);
        ParticleSpawner.displayParticles(target.getWorld(), Particle.DUST, target.getEyeLocation().subtract(0, target.getHeight() / 2, 0), 8, .3, target.getHeight() / 2, .3, 0, dust, 200);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        String text = pathway.getColorPrefix() + (casting.contains(beyonder) ? "Active" : "Inactive");

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
