package de.jakob.lotm.pathways.abilities.other_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class SpiritBlast extends Ability {

    private final Set<Beyonder> casting = new HashSet<>();

    public SpiritBlast(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        customModelData = id + "_ability";
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(casting.contains(beyonder))
            return;

        LivingEntity entity = beyonder.getEntity();
        LivingEntity target = getTargetEntity(entity, 9);

        if(target == null)
            return;

        casting.add(beyonder);

        runTaskWithDuration(2, 20 * 4, () ->{
            if(target.getWorld() != entity.getWorld() || !target.isValid())
                return;
            target.damage(.75, entity);
            ParticleUtil.drawLine(entity.getEyeLocation(), target.getEyeLocation().subtract(0, .4, 0), Particle.COMPOSTER, .33, entity.getWorld(), null, 4, .02);
        }, () -> casting.remove(beyonder));
    }
}
