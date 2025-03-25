package de.jakob.lotm.pathways.beyonder;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.util.lotm.NPCNameLookup;
import de.jakob.lotm.util.minecraft.EntityUtil;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.UUID;


public class BeyonderNPC extends Beyonder{

    @Getter
    private final boolean hostile;

    @Getter
    private final NPC npc;
    private final Random random = new Random();
    private final boolean dummy;

    public BeyonderNPC(Location location, Pathway currentPathway, int currentSequence, boolean hostile, boolean dummy) {
        super(null, currentPathway, currentSequence);

        String name = NPCNameLookup.getRandomName();
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER,  "§r§l" + currentPathway.getColorPrefix() + name);
        npc.setName("§r§l" + currentPathway.getColorPrefix() + name);
        npc.spawn(location);
        npc.setProtected(false);

        npc.getEntity().getScoreboardTags().add("beyonder_npc");

        this.uuid = npc.getMinecraftUniqueId();
        this.hostile = hostile;
        this.dummy = dummy;
    }

    @Override
    protected void tick(int tick) {
        if (npc == null || !npc.isSpawned()) return;

        if(dummy) return;

        if (tick % 20 * 12 == 0 && currentTarget == null) {
            moveToRandomLocation();
        }

        searchTarget();

        if(currentTarget != null && currentTarget.getWorld() == getEntity().getWorld()) {
            if(tick % 10 == 0) {
                useAbility();
            }
            if(tick % 15 == 0) {
                npc.faceLocation(currentTarget.getLocation());
                moveToTarget();
            }

            if(tick % 30 == 0) {
                if(currentTarget.getLocation().distance(getEntity().getLocation()) < 2) {
                    getEntity().attack(currentTarget);
                }
            }
        }
    }

    private void moveToTarget() {
        double distance = currentPathway.optimalDistance(currentSequence, getEntity().getHealth());
        Vector dir = currentTarget.getLocation().toVector().subtract(getEntity().getLocation().toVector()).normalize();
        Location targetLoc = currentTarget.getLocation().subtract(dir.clone().multiply(distance));
        npc.getNavigator().setTarget(targetLoc);

        if(distance > 1.5 && currentTarget.getLocation().distance(getEntity().getLocation()) < distance - 3) {
            if(dir.lengthSquared() > 0)
                getEntity().setVelocity(dir.multiply(-1));
        }
    }

    @Override
    public UUID getUuid() {
        this.uuid = npc.getMinecraftUniqueId();
        return uuid;
    }


    private void useAbility() {
        List<Ability> usableAbilities = abilities.stream().filter(Ability::canBeUSedByNonPlayer).filter(ability -> !(ability instanceof PassiveAbility)).filter(ability -> ability.shouldUseAbility(this)).toList();
        if(!usableAbilities.isEmpty()) {
            Ability ability = usableAbilities.get(random.nextInt(usableAbilities.size()));
            ability.prepareAbility(this);
        }
    }

    private void searchTarget() {
        if (currentTarget != null && (!currentTarget.isValid() || currentTarget.getWorld() != getEntity().getWorld() || currentTarget.getLocation().distance(getEntity().getLocation()) > 40 || !EntityUtil.mayDamage(currentTarget, getEntity())[1]) || (currentTarget instanceof Player player && (player.getGameMode() == org.bukkit.GameMode.SPECTATOR || player.getGameMode() == org.bukkit.GameMode.CREATIVE))) {
            currentTarget = null;
        }

        if (currentTarget == null) {
            currentTarget = findTarget(hostile);
        }
    }

    private LivingEntity findTarget(boolean hostile) {
        World world = getEntity().getWorld();
        Location location = getEntity().getLocation();

        return world.getNearbyEntities(location, 20, 20, 20).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> entity != getEntity())
                .filter(entity -> !(entity instanceof ArmorStand))
                .filter(entity -> !(entity instanceof Player player && (player.getGameMode() == org.bukkit.GameMode.SPECTATOR || player.getGameMode() == org.bukkit.GameMode.CREATIVE)))
                .filter(entity -> EntityUtil.mayDamage(entity, getEntity())[0])
                .filter(entity -> hostile || entity instanceof Monster)
                .map(entity -> (LivingEntity) entity)
                .findFirst()
                .orElse(null);
    }

    private void moveToRandomLocation() {
        if (npc == null || !npc.isSpawned()) return;

        Location currentLocation = npc.getStoredLocation();

        // Generate random offsets
        double xOffset = (random.nextDouble() - 0.5) * 10; // Between -5 and 5
        double zOffset = (random.nextDouble() - 0.5) * 10;

        Location targetLocation = currentLocation.clone().add(xOffset, 0, zOffset);

        // Ensure the NPC moves smoothly
        npc.getNavigator().setTarget(targetLocation);
    }

    @Override
    public LivingEntity getEntity() {
        return npc != null ? (LivingEntity) npc.getEntity() : null;
    }

    private boolean hasDied = false;

    private void onDeath(Location location) {
        if(hasDied)
            return;

        hasDied = true;

        LOTM.getInstance().removeBeyonder(getUuid());

        npc.destroy();
        CitizensAPI.getNPCRegistry().deregister(npc);

        ItemStack characteristic = currentPathway.getCharacteristicForSequence(getCurrentSequence());
        if(characteristic == null)
            return;

        if(location.getWorld() == null)
            return;

        location.getWorld().dropItemNaturally(location, characteristic);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity() != getEntity()) return;

        if(npc == null) return;

        if(event.getDamager() instanceof LivingEntity livingEntity) {
            currentTarget = livingEntity;
        }

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            if(getEntity() == null || getEntity().getHealth() <= 0 || getEntity().isDead())
                onDeath(event.getEntity().getLocation());
        }, 1);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() != getEntity()) return;

        if(npc == null) return;

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            if(getEntity() == null || getEntity().getHealth() <= 0 || getEntity().isDead())
                onDeath(event.getEntity().getLocation());
        }, 1);
    }
}
