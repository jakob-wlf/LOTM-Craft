package de.jakob.lotm.pathways.impl.sun.impl;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class GazeOfPurification extends ToggleableAbility {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 191, 71), 2.3f);

    public GazeOfPurification(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        tickDelay = 6;
    }

    //TODO: Sounds
    @Override
    protected void impl(Beyonder beyonder) {
        if(!beyonder.removeSpirituality(28)) {
            casting.remove(beyonder);
            return;
        }

        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = getTargetEntity(entity, 30, true, .65, false);

        if(target == null)
            return;

        target.damage(32, entity);
        target.setNoDamageTicks(10);

        ParticleSpawner.displayParticles(target.getWorld(), Particle.END_ROD, target.getEyeLocation().subtract(0, target.getHeight() / 2, 0), 40, .3, target.getHeight() / 2, .3, 0, 200);
        ParticleSpawner.displayParticles(target.getWorld(), Particle.DUST, target.getEyeLocation().subtract(0, target.getHeight() / 2, 0), 40, .3, target.getHeight() / 2, .3, 0, dust, 200);

    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        String text = pathway.getColorPrefix() + (casting.contains(beyonder) ? "Active" : "Inactive");

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
