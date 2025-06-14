package de.jakob.lotm.pathways.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public abstract class ToggleableAbility extends Ability {

    private final HashSet<Beyonder> onCooldown = new HashSet<>();


    protected int spiritualityCost = -1;

    protected int constantSpiritualityCost = -1;

    protected int tickDelay = 1;

    protected final HashSet<Beyonder> casting = new HashSet<>();

    protected boolean allowDifferentWorld = false;
    protected boolean displayActiveStatus = true;
    protected int cooldownTicksAfterDisable = 0;

    public ToggleableAbility(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
        init();
    }

    public ToggleableAbility() {
        super();
        init();
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder)) {
            casting.remove(beyonder);
            return;
        }

        if(onCooldown.contains(beyonder)) {
            return;
        }

        if(spiritualityCost > 0) {
            if(!beyonder.removeSpirituality(spiritualityCost))
                return;
        }


        casting.add(beyonder);
        start(beyonder);

        if(!casting.contains(beyonder)) {
            return;
        }

        new BukkitRunnable() {

            int copyCounter = 0;

            @Override
            public void run() {
                if(abilityType != AbilityType.SEQUENCE_PROGRESSION && copyCounter > ((float) (20 * 28) / tickDelay))
                    casting.remove(beyonder);

                if(beyonder.getEntity() == null || !beyonder.getEntity().isValid() || beyonder.getCurrentPathway() != pathway || beyonder.getCurrentSequence() > sequence) {
                    casting.remove(beyonder);
                }

                if(constantSpiritualityCost > 0 && !beyonder.removeSpirituality(constantSpiritualityCost)) {
                    casting.remove(beyonder);
                }

                if(!casting.contains(beyonder)) {
                    if(cooldownTicksAfterDisable > 0) {
                        onCooldown.add(beyonder);
                        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
                            onCooldown.remove(beyonder);
                        }, cooldownTicksAfterDisable);
                    }
                    stop(beyonder);
                    cancel();
                    return;
                }

                impl(beyonder);

                if(abilityType != AbilityType.SEQUENCE_PROGRESSION) {
                    copyCounter++;
                }
            }
        }.runTaskTimer(LOTM.getInstance(), 0, tickDelay);
    }

    protected void start(Beyonder beyonder) {
    }

    protected void impl(Beyonder beyonder) {

    }

    protected void stop(Beyonder beyonder) {

    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        return !casting.contains(beyonder);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!displayActiveStatus) {
            return;
        }

        String text = pathway.getColorPrefix() + (casting.contains(beyonder) ? "Active" : "Inactive");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }
}
