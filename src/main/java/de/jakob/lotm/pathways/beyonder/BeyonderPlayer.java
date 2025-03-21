package de.jakob.lotm.pathways.beyonder;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.util.lotm.BeyonderInventoryHolder;
import de.jakob.lotm.util.minecraft.EntityUtil;
import de.jakob.lotm.util.minecraft.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BeyonderPlayer extends Beyonder{

    private final BossBar bossBar;
    private Ability currentAbility;

    private final Set<BarFlag> barFlags = new HashSet<>();

    public BeyonderPlayer(UUID uuid, Pathway currentPathway, int currentSequence) {
        super(uuid, currentPathway, currentSequence);

        bossBar = Bukkit.createBossBar("Spirituality", BarColor.BLUE, BarStyle.SOLID);

        if(getPlayer() != null)
            TextUtil.displayMessageWithCorrectLineBreaks(getPlayer(), TextUtil.helpMessage);
    }

    @Override
    protected void tick(int tick) {
        Player player = getPlayer();
        if(player == null || !player.isValid())
            return;

        if(maxSpirituality != 0)
            bossBar.setProgress((double) spirituality / maxSpirituality);

        if(tick % 60 == 0) {
            refreshBossBar(player.getInventory().getItemInMainHand());
        }

        if(tick % 5 == 0) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if(currentAbility == null || !heldItem.isSimilar(currentAbility.getItem())) {
                currentAbility = abilities.stream().filter(a -> a.getItem().isSimilar(heldItem)).findFirst().orElse(null);
            }

            if(currentAbility != null) {
                currentAbility.onHold(this, player);
            }
        }
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @EventHandler
    public void onPlayerSwitchItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if(player != getPlayer())
            return;

        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());

        Ability ability = abilities.stream().filter(a -> a.getItem().isSimilar(previousItem)).findFirst().orElse(null);
        if(ability != null)
            ability.onSwitchOutItem(this, getPlayer());

        refreshBossBar(player.getInventory().getItem(event.getNewSlot()));

    }

    private void refreshBossBar(ItemStack currentItem) {
        Ability ability = abilities.stream().filter(a -> a.getItem().isSimilar(currentItem)).findFirst().orElse(null);

        for(BarFlag flag : BarFlag.values())
            bossBar.removeFlag(flag);

        for(BarFlag barFlag : barFlags)
            bossBar.addFlag(barFlag);

        if(ability != null) {
            bossBar.addPlayer(getPlayer());
            currentAbility = ability;
        }
        else
            bossBar.removePlayer(getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(player != getPlayer())
            return;

        ItemStack itemStack = event.getItem();

        Ability ability = abilities.stream().filter(a -> a.getItem().isSimilar(itemStack)).findFirst().orElse(null);

        if(ability == null)
            return;

        event.setCancelled(true);

        if(event.getAction().toString().contains("RIGHT_CLICK")) {
            if(player.isSneaking())
                ability.sneakRightClick(this);
            else
                ability.rightClick(this);
        }
        if(event.getAction().toString().contains("LEFT_CLICK")) {
            if(player.isSneaking())
                ability.sneakLeftClick(this);
            else
                ability.leftClick(this);
        }
    }

    @EventHandler
    public void onPlayerHitEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player player))
            return;

        if(player != getPlayer())
            return;

        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if(!EntityUtil.mayDamage(entity, damager)[0]) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        Ability ability = abilities.stream().filter(a -> a.getItem().isSimilar(itemStack)).findFirst().orElse(null);

        if(ability == null || !ability.hasHitAbility() || damager.getWorld() != entity.getWorld() || entity.getLocation().distance(damager.getLocation()) > 3)
            return;

        event.setCancelled(true);

        ability.onHit(this);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if(event.getPlayer() != getPlayer())
            return;

        Player player = event.getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        Ability ability = abilities.stream().filter(a -> a.getItem().isSimilar(itemStack)).findFirst().orElse(null);

        if(ability == null)
            return;

        if(!event.isSneaking())
            return;

        ability.sneakToggle(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = getPlayer();

        if(event.getWhoClicked() != player)
            return;

        if(event.getClickedInventory() == null || !(event.getClickedInventory().getHolder() instanceof BeyonderInventoryHolder))
            return;

        if(event.getCurrentItem() == null)
            return;

        event.setCancelled(true);

        Ability ability = getAbilities().stream().filter(a -> event.getCurrentItem().isSimilar(a.getItem())).findFirst().orElse(null);

        if(ability == null || ability instanceof PassiveAbility)
            return;

        ItemStack abilityItem = ability.getItem();

        if(abilityItem == null || (player.getInventory().contains(abilityItem) && ability.getAbilityType() == AbilityType.SEQUENCE_PROGRESSION))
            return;

        player.getInventory().addItem(abilityItem);

        refreshBossBar(player.getInventory().getItemInMainHand());
    }

    public void addBarFlag(BarFlag barFlag) {
        barFlags.add(barFlag);
    }

    public void removeBarFlag(BarFlag barFlag) {
        barFlags.remove(barFlag);
    }

    public void addBarFlag(BarFlag barFlag, int ticks) {
        barFlags.add(barFlag);
        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> barFlags.remove(barFlag), ticks);
    }
}
