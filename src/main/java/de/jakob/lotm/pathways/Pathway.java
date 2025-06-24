package de.jakob.lotm.pathways;

import de.jakob.lotm.BeyonderConfigManager;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.TextUtil;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class Pathway {

    @Getter
    protected String displayName;
    @Getter
    protected String name;
    protected String colorPrefix;
    @Getter
    protected Color color;
    @Getter
    protected Set<Pathway> neighbouring;
    protected HashMap<Integer, Ability[]> abilities;
    @Getter
    protected ItemStack[] characteristicItems;

    @Getter
    protected Potions potions;

    @Getter
    protected HashMap<Integer, ItemStack[]> potionIngredients = new HashMap<>();

    @Getter
    protected String[] sequenceNames;

    public Pathway(String displayName, String name, String colorPrefix, Color color) {
        this.displayName = displayName;
        this.name = name;
        this.colorPrefix = colorPrefix;
        this.color = color;

        for(int i = 0; i < 10; i++) {
            potionIngredients.put(i, new ItemStack[]{});
        }

        initSequenceNames();
        initPotions();
        initPotIngredients();
        initCharacteristics();
    }

    public String getColorPrefix() {
        return  "§" + colorPrefix;
    }

    private void initPotions() {
        potions = new Potions(this);
    }
    protected abstract void initSequenceNames();

    protected abstract void initPotIngredients();

    private void initCharacteristics() {
        characteristicItems = new ItemStack[9];
        for(int i = 0; i < 9; i++) {
            characteristicItems[i] = getCharacteristicForSequence(i + 1);
        }
    }

    public ItemStack getCharacteristicForSequence(int sequence) {
        ItemStack head = ItemsUtil.createCustomHead(getURL());
        ItemMeta itemMeta = head.getItemMeta();
        if(itemMeta == null)
            return head;
        itemMeta.setDisplayName(getColorPrefix() + "§lBeyonder Characteristic - " + sequenceNames[sequence]);
        itemMeta.setLore(List.of(
                getColorPrefix() + "§l" + displayName + " Pathway",
                getColorPrefix() + "§lSequence " + sequence)
        );
        itemMeta.setCustomModelData(550);

        head.setItemMeta(itemMeta);

        return head;
    }

    protected String getURL() {
        return "http://textures.minecraft.net/texture/e9469a2f7a98593a4b0bd6383d70fcf105904a930beaa0671a999b1930b94953";
    }

    public void setSequencePathway(Beyonder beyonder, int sequence, boolean includePreviousSequences) {
        beyonder.clearAbilities();
        beyonder.setSequencePathway(this, sequence);

        addAbilities(beyonder, sequence, includePreviousSequences);
    }

    public void addAbilities(Beyonder beyonder, int sequence, boolean includePreviousSequences) {
        if(sequence > 9 || sequence < 1)
            return;

        if(beyonder instanceof BeyonderPlayer)
            ((BeyonderPlayer) beyonder).getPlayer().sendMessage(getColorPrefix() + "§lNew abilities unlocked! §8#########################################");

        if(includePreviousSequences) {
            for(int i = 9; i > sequence; i--) {
                if(!abilities.containsKey(i))
                    continue;

                for(Ability ability : abilities.get(i)) {
                    if(!beyonder.getAbilities().contains(ability)) {
                        beyonder.addAbility(ability);
                        if(beyonder instanceof BeyonderPlayer) {
                            TextUtil.displayAbilityInfoWithCorrectLineBreaks(((BeyonderPlayer) beyonder).getPlayer(), ability.getDescription());
                        }
                    }
                }
            }
        }

        if(!abilities.containsKey(sequence))
            return;

        for(Ability ability : abilities.get(sequence)) {
            beyonder.addAbility(ability);
            if(beyonder instanceof BeyonderPlayer) {
                TextUtil.displayAbilityInfoWithCorrectLineBreaks(((BeyonderPlayer) beyonder).getPlayer(), ability.getDescription());
            }
        }

        beyonder.sortAbilities();

        if(beyonder instanceof BeyonderPlayer)
            BeyonderConfigManager.saveBeyonders();
    }

    public void addAbilities(Beyonder beyonder, int sequence) {
        addAbilities(beyonder, sequence, true);
    }

    public void addAbilities(Beyonder beyonder, List<String> abilityIds) {
        for(String abilityId : abilityIds) {
            Ability ability = getAbility(abilityId);
            if(ability == null)
                continue;
            beyonder.addAbility(ability);
        }
    }

    public Ability getAbility(String id) {
        for(Ability[] abilitiesPerSequence : abilities.values()) {
            for(Ability ability : abilitiesPerSequence) {
                if(ability.getId().equals(id))
                    return ability;
            }
        }
        return null;
    }

    public List<Ability> getAbilities() {
        List<Ability> allAbilities = new ArrayList<>();
        for(Ability[] abilitiesPerSequence : abilities.values()) {
            allAbilities.addAll(List.of(abilitiesPerSequence));
        }

        return allAbilities;
    }

    public double optimalDistance(int sequence, double health) {
        return 5;
    }


}
