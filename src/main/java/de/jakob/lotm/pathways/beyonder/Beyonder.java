package de.jakob.lotm.pathways.beyonder;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.PassiveAbility;
import de.jakob.lotm.util.lotm.Lookup;
import de.jakob.lotm.util.minecraft.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class Beyonder implements Listener {

    protected final List<Ability> abilities;

    @Getter
    protected UUID uuid;
    @Getter
    protected Pathway currentPathway;
    @Getter
    protected int currentSequence;
    protected int spirituality;
    protected int maxSpirituality;

    protected boolean griefing;

    @Getter @Setter
    protected LivingEntity currentTarget = null;

    private final HashMap<Integer, Double> sequenceMultipliers;
    private final HashMap<Integer, Integer> spiritualityLookup;

    private final HashMap<PotionEffectType, Integer> passiveEffects;

    private final ArrayList<Double> multiplierModifiers;

    private boolean isLoosingControl = false;

    protected double digestion = 0;

    @Getter
    private boolean mayUsePowers = true;

    public Beyonder(UUID uuid, Pathway currentPathway, int currentSequence) {
        this.uuid = uuid;
        this.currentPathway = currentPathway;
        this.currentSequence = currentSequence;

        LOTM.getInstance().registerListener(this);

        griefing = false;

        abilities = new ArrayList<>();

        sequenceMultipliers = new HashMap<>(Map.of(
                9, 1.0,
                8, 1.0,
                7, 1.1,
                6, 1.1,
                5, 1.2,
                4, 1.65,
                3, 1.65,
                2, 1.95,
                1, 2.8
        ));
        spiritualityLookup = new HashMap<>(Map.of(
                        9, 180,
                        8, 200,
                        7, 780,
                        6, 1200,
                        5, 1850,
                        4, 3850,
                        3, 5000,
                        2, 9000,
                        1, 15000
                ));
        passiveEffects = new HashMap<>();
        multiplierModifiers = new ArrayList<>();

        refreshMaxSpirituality();
        spirituality = maxSpirituality;

        boolean isPlayer = this instanceof BeyonderPlayer;

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            if(getEntity() != null)
                getEntity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 255, false, false, false));
        }, 20);

        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                if(getEntity() == null || !getEntity().isValid()) {
                    if(!isPlayer) {
                        destroyBeyonder();
                        cancel();
                    }
                    return;
                }

                tick(counter);
                loop(counter);
                counter++;
                if(counter > 20 * 60 * 60)
                    counter = 0;
            }
        }.runTaskTimer(LOTM.getInstance(), 0, 1);
    }

    @EventHandler
    public void onEntityHitEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() != getEntity())
            return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if(!EntityUtil.mayDamage(entity, damager)[0]) {
            event.setCancelled(true);
            return;
        }

        for(Ability ability : abilities) {
            if(!(ability instanceof PassiveAbility passiveAbility))
                continue;

            Entity damaged = event.getEntity();
            if(!(damaged instanceof LivingEntity livingEntity))
                continue;
            passiveAbility.onHit(this, livingEntity);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() != getEntity())
            return;

        for(Ability ability : abilities) {
            if(!(ability instanceof PassiveAbility passiveAbility))
                continue;

            passiveAbility.onDamage(this, event);
        }
    }

    /**Loop that executes for all Beyonder Children and should not be overwritten*/
    protected void loop(int tick) {
        if(getEntity() == null)
            return;

        if(tick % 2 == 0) {
            for(Ability ability : abilities) {
                ability.tick(this);
            }
        }

        if(tick % 5 == 0) {
            for(Ability ability : abilities) {
                if (!(ability instanceof PassiveAbility passiveAbility))
                    continue;

                passiveAbility.tick(this);
            }
        }

        if(tick % 10 == 0) {
            addSpirituality(Math.max(Math.round(maxSpirituality / (2f * 60f * 4f)), 1));
        }

        if(tick % 80 == 0) {
            if(!(this instanceof BeyonderPlayer) && getEntity() != null) {
                Player player = LOTM.getInstance().getNearestPlayer(getEntity().getLocation());
                if((player == null || player.getLocation().distance(getEntity().getLocation()) > 450) && !isAnyPlayerExiled()) {
                    destroyBeyonder();
                    return;
                }
            }
        }

        if(tick % 40 == 0) {
            passiveEffects.clear();
            for(Ability ability : abilities) {
                if(!(ability instanceof PassiveAbility passiveAbility))
                    continue;

                PotionEffect[] effects = passiveAbility.getPotionEffect(this);
                if(effects == null)
                    continue;

                for(PotionEffect effect : effects) {
                    if(passiveEffects.containsKey(effect.getType()) && passiveEffects.get(effect.getType()) < effect.getAmplifier())
                        passiveEffects.replace(effect.getType(), effect.getAmplifier());
                    else if (!passiveEffects.containsKey(effect.getType()))
                        passiveEffects.put(effect.getType(), effect.getAmplifier());
                }
            }

            for(Map.Entry<PotionEffectType, Integer> entry : passiveEffects.entrySet()) {
                getEntity().addPotionEffect(new PotionEffect(entry.getKey(), 20 * 14, entry.getValue(), false, false, false));
            }
        }

        if(tick % 100 == 0) {
            digestPotion(getCurrentSequence());
        }

        if(tick % 400 == 0) {
            refreshMaxSpirituality();
        }
    }

    public void useRandomAbility() {
        Random random = new Random();
        List<Ability> usableAbilities = abilities.stream().filter(Ability::canBeUSedByNonPlayer).filter(ability -> !(ability instanceof PassiveAbility)).toList();
        if(!usableAbilities.isEmpty()) {
            Ability ability = usableAbilities.get(random.nextInt(usableAbilities.size()));
            ability.prepareAbility(this);
        }
    }


    public void disablePowers(int ticks) {
        mayUsePowers = false;
        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            mayUsePowers = true;
        }, ticks);
    }

    public void addAbility(Ability ability) {
        if(abilities.contains(ability))
            return;

        abilities.add(ability);
        abilities.sort(Comparator.comparing(Ability::getSequence));
    }

    public LivingEntity getEntity() {
        Entity entity = Bukkit.getEntity(uuid);
        if(entity instanceof LivingEntity livingEntity && entity.isValid())
            return livingEntity;

        return null;
    }

    private boolean isAnyPlayerExiled() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getScoreboardTags().contains("exiled"))
                return true;
        }

        return false;
    }
    public void digestPotion(int abilitySequence) {
        if (digestion == 1)
            return;

        double digestionIncrease = getDigestionIncrease(abilitySequence);

        digestion += digestionIncrease;

        if (digestion > 1) {
            digestion = 1;
            if (getEntity() instanceof Player player) {
                player.playSound(player, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1, 1);
                player.playSound(player, Sound.ENTITY_WITCH_DRINK, 1, .8f);
                player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, .5f);
                player.spawnParticle(Particle.ELECTRIC_SPARK, player.getEyeLocation().subtract(0, .4, 0), 200, .5, 1, .5, 0);
                player.spawnParticle(Particle.WITCH, player.getEyeLocation().subtract(0, .4, 0), 200, .5, 1, .5, 0);
                player.sendMessage("§aYou have digested the potion.");
            }
        }
    }

    private double getDigestionIncrease(int abilitySequence) {
        int currentSequence = getCurrentSequence();

        // Normalize abilitySequence (1-9) so that lower values have more impact
        double abilityFactor = (10.0 - abilitySequence) / 10.0;

        // Normalize currentSequence so that higher values make digestion harder
        double sequenceFactor = 1.0 / (1.0 + currentSequence * 0.2); // More sequence -> Less digestion

        // Adjusted digestion formula: current sequence has stronger effect
        return 0.01 * currentSequence * abilityFactor * sequenceFactor;
    }

    public void addMultiplierModifier(double modifier, int ticks) {
        double difference = getCurrentMultiplier() * modifier - getCurrentMultiplier();
        multiplierModifiers.add(difference);

        new BukkitRunnable() {

            @Override
            public void run() {
                multiplierModifiers.remove(difference);
            }
        }.runTaskLater(LOTM.getInstance(), ticks);
    }

    public int addMultiplierModifier(double modifier) {
        double difference = getCurrentMultiplier() * modifier - getCurrentMultiplier();
        multiplierModifiers.add(difference);
        return multiplierModifiers.indexOf(difference);
    }

    public void removeMultiplierModifier(int index) {
        multiplierModifiers.remove(index);
    }

    public void removeAbility(Ability ability) {
        abilities.remove(ability);
    }

    public void clearAbilities() {
        abilities.clear();
    }

    protected abstract void tick(int tick);

    public boolean removeSpirituality(int amount) {
        if(!(this instanceof BeyonderPlayer))
            amount /= 3;
        if(amount > spirituality)
            return false;

        spirituality -= amount;
        return true;
    }

    public void addSpirituality(int amount) {
        if((amount + spirituality) > maxSpirituality)
            spirituality = maxSpirituality;
        else
            spirituality += amount;
    }

    public void setSequencePathway(Pathway pathway, int sequence) {
        setCurrentPathway(pathway);
        setCurrentSequence(sequence);

        if(this instanceof BeyonderPlayer) {
            digestion = 0;
        }

        refreshMaxSpirituality();
        spirituality = maxSpirituality;
    }

    public void advance(Pathway pathway, int sequence, boolean includePrevious, boolean chanceOfLooseControl) {
        int sequenceDifference = currentSequence >= sequence ? currentSequence - sequence : sequence - currentSequence;
        boolean compatiblePathway = currentPathway == null || pathway == currentPathway || (currentPathway.getNeighbouring() != null && (currentPathway.getNeighbouring().contains(pathway)));

        if(currentSequence >= sequence) {
            setCurrentPathway(pathway);
            setCurrentSequence(sequence);
        }

        pathway.addAbilities(this, sequence, includePrevious);

        if(chanceOfLooseControl) {
            if(digestion < 1 && getCurrentSequence() != 9)
                looseControl(.6);
            else if(sequenceDifference > 1)
                looseControl(.65);
            else if(!compatiblePathway)
                looseControl(.7);
            else
                looseControl(.02);
        }

        if(this instanceof BeyonderPlayer) {
            digestion = 0;
        }

        refreshMaxSpirituality();
        spirituality = maxSpirituality;
    }

    public void destroyBeyonder() {
        LOTM.getInstance().getSpawnedBeyonders().remove(this);
        LOTM.getInstance().removeBeyonder(uuid);

        if(this instanceof BeyonderNPC beyonderNPC) {
            beyonderNPC.getNpc().destroy();
            CitizensAPI.getNPCRegistry().deregister(beyonderNPC.getNpc());
        }

        if(getEntity() != null)
            getEntity().remove();
    }
    public void looseControl(double looseControlProbability) {
        if(getEntity() instanceof Player player && (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) || isLoosingControl) {
            return;
        }

        isLoosingControl = true;

        getEntity().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 15, 4));
        getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 15, 4));
        getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 15, 4));

        Bukkit.getScheduler().runTaskLater(LOTM.getInstance(), () -> {
            isLoosingControl = false;
            Random random = new Random();
            if(random.nextDouble() < looseControlProbability) {
                spawnRampager(getEntity().getLocation());
                setSequencePathway(null, 10);
                clearAbilities();
                getEntity().setHealth(0);
            }
        }, 20 * 15);
    }

    private void spawnRampager(Location location) {
        World world = location.getWorld();

        if(world == null)
            return;

        Entity entity = world.spawnEntity(location, Lookup.getRampagerEntityForPathway(getCurrentPathway()));
        if(currentPathway != null)
            entity.setCustomName(currentPathway.getColorPrefix() + "§lRampager");
        else
            entity.setCustomName("§8§lRampager");
        entity.setCustomNameVisible(true);
        entity.setPersistent(true);

        LOTM.getInstance().createBeyonder(entity.getUniqueId(), getCurrentPathway(), getCurrentSequence(), true, true, true);
    }

    public void refreshSpirituality() {
        spirituality = maxSpirituality;
    }

    public void refreshMaxSpirituality() {
        maxSpirituality = getCurrentMaxSpirituality();
    }

    public double getMultiplierForSequence(int sequence) {
        return sequenceMultipliers.get(sequence);
    }

    public double getCurrentMultiplier() {
        if(!sequenceMultipliers.containsKey(currentSequence))
            return 1.0;
        double multiplier = sequenceMultipliers.get(currentSequence);
        for(double modifier : multiplierModifiers)
            multiplier += modifier;
        return multiplier;
    }

    public void setCurrentPathway(Pathway currentPathway) {
        this.currentPathway = currentPathway;
    }

    public void setCurrentSequence(int currentSequence) {
        this.currentSequence = currentSequence;
    }

    public int getMaxSpiritualityForSequence(int sequence) {
        return spiritualityLookup.get(sequence);
    }

    public int getCurrentMaxSpirituality() {
        return spiritualityLookup.getOrDefault(currentSequence, 100);
    }

    public List<Ability> getAbilities() {
        return new ArrayList<>(abilities);
    }

    public void sortAbilities() {
        abilities.sort(Comparator.comparing(Ability::getSequence).reversed());
    }

    public boolean isGriefingEnabled() {
        return griefing;
    }

    public void setGriefingEnabled(boolean griefing) {
        this.griefing = griefing;
    }

}
