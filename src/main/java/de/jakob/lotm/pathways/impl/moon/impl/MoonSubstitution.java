package de.jakob.lotm.pathways.impl.moon.impl;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@NoArgsConstructor
public class MoonSubstitution extends Ability implements Listener {

    private final HashMap<UUID, Integer> createdSubstitutions = new HashMap<>();
    private final HashSet<Beyonder> onCooldown = new HashSet<>();

    private final Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(232, 26, 63), 2.3f);
    private final Particle.DustOptions dust2 = new Particle.DustOptions(Color.fromRGB(30, 30, 30), 1.4f);

    public MoonSubstitution(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material, description, id);

        LOTM.getInstance().registerListener(this);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        if(onCooldown.contains(beyonder)) {
            if(entity instanceof Player p)
                p.sendMessage("§cCan't create any more substitutions");
            return;
        }

        if(createdSubstitutions.containsKey(entity.getUniqueId()) && createdSubstitutions.get(entity.getUniqueId()) >= 5) {
            if(entity instanceof Player p)
                p.sendMessage("§cCan't create any more substitutions");
            return;
        }

        if(!createdSubstitutions.containsKey(entity.getUniqueId()))
            createdSubstitutions.put(entity.getUniqueId(), 1);
        else
            createdSubstitutions.replace(entity.getUniqueId(), createdSubstitutions.get(entity.getUniqueId()) + 1);

        if(createdSubstitutions.get(entity.getUniqueId()) >= 5) {
            onCooldown.add(beyonder);
            Bukkit.getScheduler().runTaskLater(plugin, () -> onCooldown.remove(beyonder), 20 * 60 * 5);
        }

        ItemStack itemStack = getPaperItem();
        if(entity instanceof Player player)
            player.getInventory().addItem(itemStack);

        entity.getWorld().playSound(entity, Sound.ENTITY_EVOKER_CAST_SPELL, 1, .5f);
        entity.getWorld().playSound(entity, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, .5f);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!createdSubstitutions.containsKey(event.getEntity().getUniqueId()) || createdSubstitutions.get(event.getEntity().getUniqueId()) < 1)
            return;

        createdSubstitutions.replace(event.getEntity().getUniqueId(), createdSubstitutions.get(event.getEntity().getUniqueId()) - 1);

        Entity entity = event.getEntity();
        event.setCancelled(true);
        Location oldLoc = entity.getLocation().clone();
        Location newLoc = entity.getLocation().add(random.nextInt(-6, 6), random.nextInt(2), random.nextInt(-6, 6));
        int breakoutCounter = 100;
        while (newLoc.getBlock().getType().isSolid() && breakoutCounter > 0) {
            newLoc = entity.getLocation().add(random.nextInt(-6, 6), random.nextInt(2), random.nextInt(-6, 6));
            breakoutCounter--;
        }

        if(LOTM.getInstance().isCitizensEnabled()) {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(entity.getType(), entity.getName());
            npc.spawn(oldLoc);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                npc.destroy();
                CitizensAPI.getNPCRegistry().deregister(npc);
            }, 35);
        }

        entity.teleport(newLoc);
        ParticleSpawner.displayParticles(entity.getWorld(), Particle.DUST, entity.getLocation().add(0, .55, 0), 25, .2, .8, .2, 0, dust, 150);
        ParticleSpawner.displayParticles(entity.getWorld(), Particle.DUST, entity.getLocation().add(0, .55, 0), 25, .2, .8, .2, 0, dust2, 150);
        entity.getWorld().playSound(newLoc, Sound.ENTITY_GOAT_HORN_BREAK, 1, 1);
        entity.getWorld().playSound(newLoc, Sound.ENTITY_EVOKER_CAST_SPELL, 1, 1);
        entity.getWorld().playSound(newLoc, Sound.BLOCK_ANVIL_LAND, .05f, .1f);

        if(entity instanceof Player player) {
            ItemStack item = getPaperItem();

            // Remove only one instance of the item
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null && ItemsUtil.isSimilar(contents[i], item)) {
                    player.getInventory().setItem(i, null);
                    break; // Exit loop after removing one instance
                }
            }
        }
    }

    private ItemStack getPaperItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§4§lMoon Paper Figurine");
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setMaxStackSize(1);
        }
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.BANE_OF_ARTHROPODS, 5);
        return item;
    }

    @Override
    public boolean shouldUseAbility(Beyonder beyonder) {
        if(onCooldown.contains(beyonder))
            return false;

        return !createdSubstitutions.containsKey(beyonder) || createdSubstitutions.get(beyonder) < 3;
    }
}
