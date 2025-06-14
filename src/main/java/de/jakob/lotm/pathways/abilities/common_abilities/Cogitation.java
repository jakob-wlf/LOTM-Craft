package de.jakob.lotm.pathways.abilities.common_abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.ToggleableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@NoArgsConstructor
public class Cogitation extends ToggleableAbility {

    public Cogitation(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        tickDelay = 5;

        customModelData = "cogitation_ability";

        canBeUsedByNonPlayer = false;
        canBeCopied = false;
    }

    @Override
    protected void start(Beyonder beyonder) {

    }

    @Override
    protected void impl(Beyonder beyonder) {
        beyonder.addSpirituality(beyonder.getCurrentMaxSpirituality() / (4 * 40));

        beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 5, false, false, false));
        beyonder.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false, false));
    }
}
