package de.jakob.lotm.pathways.abilities;

import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class SelectableAbility extends Ability {

    protected final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    protected String[] abilities;
    protected HashMap<Integer, Integer> spiritualityCost;

    public SelectableAbility(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        init();
        initAbilities();
        initSpiritualityCost();
    }

    public SelectableAbility() {
        super();

        init();
        initAbilities();
        initSpiritualityCost();
    }


    protected abstract void initAbilities();
    protected abstract void initSpiritualityCost();

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder) && beyonder instanceof BeyonderPlayer)
            selectedAbilities.put(beyonder, 0);

        int selectedAbility = beyonder instanceof BeyonderPlayer ? selectedAbilities.get(beyonder) : random.nextInt(abilities.length);

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(beyonder, selectedAbility);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > abilities.length - 1)
            selectedAbilities.replace(beyonder, 0);
    }

    protected abstract void castAbility(Beyonder beyonder, int ability);

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(pathway.getColorPrefix() + abilities[selectedAbilities.get(beyonder)]));
    }
}
