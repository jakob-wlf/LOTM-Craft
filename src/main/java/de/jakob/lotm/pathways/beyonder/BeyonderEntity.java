package de.jakob.lotm.pathways.beyonder;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.util.minecraft.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BeyonderEntity extends Beyonder {
    Random random = new Random();

    @Getter
    private final boolean hostile;
    @Getter @Setter
    private boolean dropsCharacteristic = true;
    @Setter @Getter
    private boolean defaultAI = false;
    @Getter @Setter
    private double optimalDistance = 2;

    public BeyonderEntity(UUID uuid, Pathway currentPathway, int currentSequence, boolean hostile) {
        super(uuid, currentPathway, currentSequence);
        this.hostile = hostile;

        digestion = 1;
    }


    @Override
    protected void tick(int tick) {
        if(!getEntity().hasAI())
            return;

        searchTarget();

        if(tick % 20 == 0)
            getEntity().getWorld().loadChunk(getEntity().getLocation().getChunk());

        if(currentTarget != null && currentTarget.getWorld() == getEntity().getWorld()) {
            if(tick % 10 == 0) {
                useAbility();
            }
        }

        if(tick % 5 == 0 && !defaultAI) {
            move();
        }
    }

    public LivingEntity getMasterEntity() {
        if(hostile)
            return null;
        if(getEntity() == null)
            return null;
        for(String tag : getEntity().getScoreboardTags()) {
            if(tag.startsWith("belongs_to_")) {
                UUID masterUUID = UUID.fromString(tag.replace("belongs_to_", ""));
                Entity entity = Bukkit.getEntity(masterUUID);
                if(entity == null){
                    Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
                        if(getEntity() != null)
                            getEntity().getScoreboardTags().remove(tag);
                    }, 2);
                    return null;
                }

                if(entity.getWorld() != getEntity().getWorld()) {
                    Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
                        if(getEntity() != null)
                            getEntity().getScoreboardTags().remove(tag);
                    }, 2);
                    return null;
                }

                if((!(entity instanceof LivingEntity livingEntity) || !entity.isValid())) {
                    if(getEntity() != null)
                        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
                            if(getEntity() != null) {
                                getEntity().getScoreboardTags().remove(tag);
                            }
                        }, 2);
                    return null;
                }

                return livingEntity;
            }
        }
        return null;
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() == getMasterEntity() && (event.getEntity() instanceof LivingEntity)) {
            currentTarget = (LivingEntity) event.getEntity();
            return;
        }

        if(event.getEntity() == getMasterEntity() && (event.getDamager() instanceof LivingEntity) && EntityUtil.mayDamage(event.getDamager(), getEntity())[1]) {
            currentTarget = (LivingEntity) event.getDamager();
            return;
        }

        if(event.getEntity() == getEntity() && event.getDamager() != getMasterEntity() && event.getDamager() instanceof LivingEntity && EntityUtil.mayDamage(event.getDamager(), getEntity())[1] && EntityUtil.mayDamage(getEntity(), event.getDamager())[0]) {
            currentTarget = (LivingEntity) event.getDamager();
            return;
        }
    }

    void useAbility() {
        List<Ability> usableAbilities = abilities.stream().filter(Ability::canBeUSedByNonPlayer).filter(ability -> !(ability instanceof PassiveAbility)).filter(ability -> ability.shouldUseAbility(this)).toList();
        if(!usableAbilities.isEmpty()) {
            Ability ability = usableAbilities.get(random.nextInt(usableAbilities.size()));
            ability.prepareAbility(this);
        }
    }


    void move() {
        if(currentTarget != null) {
            if (currentTarget.getWorld() != getEntity().getWorld() || !currentTarget.isValid() || currentTarget.isDead())
                return;

            double optimalDistance = currentPathway != null ? currentPathway.optimalDistance(getCurrentSequence(), getEntity().getHealth()) : getOptimalDistance();
            double distance = getEntity().getLocation().distance(currentTarget.getLocation());

            if (distance > optimalDistance + 1 && currentTarget.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                Vector direction = currentTarget.getLocation().toVector().subtract(getEntity().getLocation().toVector());
                if (direction.lengthSquared() > 0) { // Check to prevent division by zero
                    getEntity().setVelocity(direction.normalize().multiply(0.5));
                }

                if(getEntity().getLocation().distance(currentTarget.getLocation()) <= 1.7) {
                    if(getEntity().getAttribute(Attribute.ATTACK_DAMAGE) == null) {
                        currentTarget.damage(8 * getCurrentMultiplier(), getEntity());
                    }
                }
            }
            return;
        }

        LivingEntity masterEntity = getMasterEntity();
        LivingEntity entity = getEntity();
        if(masterEntity != null && masterEntity.getWorld() == entity.getWorld() && masterEntity.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
            if(masterEntity.getLocation().distance(entity.getLocation()) > 12) {
                Vector direction = masterEntity.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
                entity.setVelocity(direction);
            }
        }
    }

    protected void searchTarget() {
        if (hostile) {
            if (currentTarget == null) {
                currentTarget = findTarget();
            }
        }

        if (currentTarget != null && (!currentTarget.isValid() || currentTarget.getWorld() != getEntity().getWorld() || currentTarget.getLocation().distance(getEntity().getLocation()) > 40 || (currentTarget instanceof Player player && (player.getGameMode() == org.bukkit.GameMode.SPECTATOR || player.getGameMode() == org.bukkit.GameMode.CREATIVE)))) {
            currentTarget = null;
        }

        if(currentTarget != null && !EntityUtil.mayDamage(currentTarget, getEntity())[1])
            currentTarget = null;

        if(getMasterEntity() == currentTarget) {
            currentTarget = null;
        }

        if(currentTarget == null && getEntity() instanceof Mob mob)
            mob.setTarget(null);

        if(currentTarget != null && getEntity() instanceof Mob mob)
            mob.setTarget(currentTarget);
    }

    private LivingEntity findTarget() {
        World world = getEntity().getWorld();
        Location location = getEntity().getLocation();

        return world.getNearbyEntities(location, 20, 20, 20).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> entity != getEntity())
                .filter(entity -> !(entity instanceof ArmorStand))
                .filter(entity -> !(entity instanceof Player player && (player.getGameMode() == org.bukkit.GameMode.SPECTATOR || player.getGameMode() == org.bukkit.GameMode.CREATIVE)))
                .filter(entity -> EntityUtil.mayDamage(entity, getEntity())[1])
                .filter(entity -> EntityUtil.mayDamage(getEntity(), entity)[0])
                .map(entity -> (LivingEntity) entity)
                .findFirst()
                .orElse(null);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getUniqueId() != uuid) {
            return;
        }

        if(!dropsCharacteristic || currentPathway == null)
            return;

        LOTM.getInstance().removeBeyonder(event.getEntity().getUniqueId());

        ItemStack characteristic = currentPathway.getCharacteristicForSequence(getCurrentSequence());
        if(characteristic == null)
            return;

        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), characteristic);
    }

}
