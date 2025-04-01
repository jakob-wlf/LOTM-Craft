package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.SelectableAbility;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class Wandering extends SelectableAbility {

    public Wandering(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        super.onHold(beyonder, player);
    }

    @Override
    protected void init() {
        canBeCopied = false;
        canBeUSedByNonPlayer = false;
    }

    @Override
    protected void initAbilities() {
        abilities = new String[] {"Overworld", "Nether", "End", "Underworld"};
    }

    @Override
    protected void initSpiritualityCost() {
        spiritualityCost = new HashMap<>(Map.of(
                0, 50,
                1, 50,
                2, 50,
                3, 50
        ));
    }

    @Override
    protected void castAbility(Beyonder beyonder, int ability) {
        beyonder.getEntity().sendMessage("§bNot yet implemented.");
    }
}
