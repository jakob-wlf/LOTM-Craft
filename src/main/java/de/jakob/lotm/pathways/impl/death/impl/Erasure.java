package de.jakob.lotm.pathways.impl.death.impl;

import com.google.common.util.concurrent.AtomicDouble;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.BlockUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class Erasure extends Ability {

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(20, 20, 20), 2);

    public Erasure(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    protected void init() {
        hasCooldown = true;
        cooldownTicks = 20 * 3;
        spirituality = 300;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location loc = entity.getLocation();

        if(loc.getWorld() == null)
            return;

        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1, 1);

        AtomicDouble i = new AtomicDouble(1);
        runTaskWithDuration(2, 20 * 6, () -> {

            if(beyonder.isGriefingEnabled())
                BlockUtil.getSphereBlocks(loc, (int) Math.round(i.get())).stream().filter(b -> b.getY() >= loc.getY()).forEach(b -> b.setType(Material.AIR));

            ParticleUtil.createParticleSphere(loc, i.get(), (int) Math.round(15 * i.get()), Particle.DUST, dust);

            getNearbyLivingEntities(entity, i.get(), loc, loc.getWorld(), true).forEach(e -> {
                Beyonder beyonderTarget = plugin.getBeyonder(e.getUniqueId());

                if(beyonderTarget == null || beyonderTarget.getCurrentSequence() > 2) {
                    if(e instanceof Player) {
                        if(e.hasMetadata("NPC")) {
                            NPC npc = CitizensAPI.getNPCRegistry().getNPC(e);
                            if(npc != null) {
                                npc.despawn();
                                npc.destroy();
                            }
                        }
                        else
                            e.setHealth(0);
                    } else {
                        e.remove();
                    }
                    return;
                }

                e.damage(80 * beyonder.getCurrentMultiplier(), beyonder.getEntity());
            });
            i.addAndGet(.8);
        }, null);
    }
}
