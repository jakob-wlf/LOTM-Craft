package de.jakob.lotm.listener;

import de.jakob.lotm.util.minecraft.ItemsUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class IngredientsDropListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity().getScoreboardTags().contains("no_drop"))
            event.getDrops().clear();

        switch(event.getEntity().getType()) {
            case WOLF -> wolfDeath(event);
            case PIG -> pigDeath(event);
            case AXOLOTL -> axolotlDeath(event);
            case CAVE_SPIDER -> caveSpiderDeath(event);
            case DROWNED -> drownedDeath(event);
            case ELDER_GUARDIAN -> elderGuardianDeath(event);
            case PARROT -> parrotDeath(event);
            case PHANTOM -> phantomDeath(event);
            case SQUID -> squidDeath(event);
            case ENDERMAN -> endermanDeath(event);
            case VEX -> vexDeath(event);
        }
    }

    private void vexDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity.getScoreboardTags().contains("ancient_wraith"))
            event.getDrops().add(ItemsUtil.wraithArtifact());
    }

    private void endermanDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity.getScoreboardTags().contains("lost_traveler"))
            event.getDrops().add(ItemsUtil.echoPearl());
    }

    private void squidDeath(EntityDeathEvent event) {
        if(random.nextInt(25) == 0) {
            event.getDrops().add(ItemsUtil.squidBlood());
        }
    }

    private void phantomDeath(EntityDeathEvent event) {
        if(random.nextInt(10) == 0) {
            event.getDrops().add(ItemsUtil.phantomWing());
        }
    }

    private void parrotDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity.getScoreboardTags().contains("blue_shadow_falcon"))
            event.getDrops().add(ItemsUtil.blueFeather());
    }

    private void elderGuardianDeath(EntityDeathEvent event) {
        event.getDrops().add(ItemsUtil.songCrystal());
    }

    private void drownedDeath(EntityDeathEvent event) {
        if(random.nextInt(15) == 0) {
            event.getDrops().add(ItemsUtil.drownedLung());
        }
    }

    private void caveSpiderDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity.getScoreboardTags().contains("hunting_spider"))
            event.getDrops().add(ItemsUtil.spiderEyes());
    }

    private void axolotlDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(entity.getScoreboardTags().contains("salamander"))
            event.getDrops().add(ItemsUtil.salamanderGland());
    }

    private void pigDeath(EntityDeathEvent event) {
        if(random.nextInt(35) == 0) {
            event.getDrops().add(ItemsUtil.boarTusk());
        }
    }

    private void wolfDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if(random.nextInt(20) == 0) {
            event.getDrops().add(ItemsUtil.wolfFang());
        }
        if(entity.getScoreboardTags().contains("gray_demonic_wolf")) {
            event.getDrops().add(ItemsUtil.wolfBlood());
            event.getDrops().add(ItemsUtil.wolfClaws());
        }
    }

}
