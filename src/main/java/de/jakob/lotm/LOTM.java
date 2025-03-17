package de.jakob.lotm;

import de.jakob.lotm.commands.*;
import de.jakob.lotm.listener.*;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.beyonder.*;
import de.jakob.lotm.pathways.impl.abyss.AbyssPathway;
import de.jakob.lotm.pathways.impl.death.DeathPathway;
import de.jakob.lotm.pathways.impl.door.DoorPathway;
import de.jakob.lotm.pathways.impl.hermit.HermitPathway;
import de.jakob.lotm.pathways.impl.hunter.RedPriestPathway;
import de.jakob.lotm.pathways.impl.moon.MoonPathway;
import de.jakob.lotm.pathways.impl.sun.SunPathway;
import de.jakob.lotm.pathways.impl.twilight_giant.TwilightGiantPathway;
import de.jakob.lotm.pathways.impl.tyrant.TyrantPathway;
import de.jakob.lotm.util.LogUtil;
import de.jakob.lotm.util.minecraft.ItemsUtil;
import lombok.Getter;
import lombok.NonNull;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.*;


public final class LOTM extends JavaPlugin {

    @Getter
    private static LOTM instance;
    private static LogUtil logger;

    @Getter
    private boolean isCitizensEnabled = false;

    @Getter
    private boolean isProtocolLibEnabled = false;
    private final HashMap<String, Pathway> pathways = new HashMap<>();
    private final HashMap<UUID, Beyonder> beyonders = new HashMap<>();

    @Getter
    private final Set<Entity> entitiesToRemove = new HashSet<>();
    @Getter
    private final HashMap<UUID, GameMode> previousGameModes = new HashMap<>();
    @Getter
    private final Set<Location> blocksToRemove = new HashSet<>();
    @Getter
    private final Set<Beyonder> removeAllowFlight = new HashSet<>();
    @Getter
    private final Set<Beyonder> removeInvisible = new HashSet<>();

    private final Set<String> scoreboardTagsToRemove = Set.of(
            "exiled",
            "siren_song",
            "no_damage",
            "protected",
            "defiling_seed",
            "wind_bound",
            "lunar",
            "gaze_of_darkness",
            "health_display",
            "see_spirits",
            "belongs_to",
            "spirit"
    );

    @Getter
    private final Set<Beyonder> resetScale = new HashSet<>();

    @Getter
    private final HashMap<UUID, Ability> abilitiesBeingUsed = new HashMap<>();

    @Getter
    private final Set<Beyonder> spawnedBeyonders = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;
        logger = new LogUtil(instance);

        logger.info("Enabled Plugin");

        checkCitizens();
        checkProtocolLib();

        registerPathways();
        registerCommands();
        registerListeners();
        registerRecipes();

        // Load Beyonders
        BeyonderConfigManager.loadBeyonders();

        Bukkit.getScheduler().runTaskLater(this, this::despawnLeftOverNPCs, 20);
    }

    private void checkProtocolLib() {
        Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (protocolLib != null && protocolLib.isEnabled()) {
            logger.info("ProtocolLib detected. Enabling ProtocolLib support.");
            isProtocolLibEnabled = true;
            return;
        }
        logger.info("ProtocolLib not detected. Disabling ProtocolLib support.");
    }

    private void registerRecipes() {
        Bukkit.addRecipe(ItemsUtil.redWineRecipe());
    }

    private void despawnLeftOverNPCs() {
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(CitizensAPI.getNPCRegistry().isNPC(entity)) {
                    NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
                    if(npc.getName().startsWith("§r§l")) {
                        npc.destroy();
                        CitizensAPI.getNPCRegistry().deregister(npc);
                    }
                }
            }
        }
    }

    private void checkCitizens() {
        Plugin citizens = Bukkit.getPluginManager().getPlugin("Citizens");
        if (citizens != null && citizens.isEnabled()) {
            logger.info("Citizens detected. Enabling Citizens support.");
            isCitizensEnabled = true;
            return;
        }
        logger.info("Citizens not detected. Disabling Citizens support.");
    }

    private void registerListeners() {
        registerListener(new DropListener());
        registerListener(new PotionListener());
        registerListener(new DamageListener());
        registerListener(new SpawnListener());
        registerListener(new DeathListener());
        registerListener(new PlaceListener());
        registerListener(new BrewListener());
        registerListener(new LootGenerateListener());
        registerListener(new IngredientsDropListener());
        registerListener(new JoinListener());
        registerListener(new SpiritSpawnListener());
        registerListener(new ProjectileShootListener());
        registerListener(new TargetListener());
    }

    public void removeListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Nullable
    public BeyonderPlayer getBeyonderPlayer(UUID uuid, boolean createIfNotPresent) {
        if(beyonders.containsKey(uuid)) {
            Beyonder beyonder = beyonders.get(uuid);
            if(beyonder instanceof BeyonderPlayer)
                return (BeyonderPlayer) beyonder;
        }
        if(createIfNotPresent)
            return (BeyonderPlayer) createBeyonder(uuid, null, 10);
        return null;
    }

    @Nullable
    public Beyonder getBeyonder(UUID uuid, boolean createIfNotPresent) {
        if(beyonders.containsKey(uuid)) {
            return beyonders.get(uuid);
        }

        if(createIfNotPresent)
            return createBeyonder(uuid, null, 10);
        return null;
    }

    @Nullable
    public BeyonderPlayer getBeyonderPlayer(UUID uuid) {
        if(beyonders.containsKey(uuid)) {
            Beyonder beyonder = beyonders.get(uuid);
            if(beyonder instanceof BeyonderPlayer)
                return (BeyonderPlayer) beyonder;
        }
        return null;
    }

    @Nullable
    public Beyonder getBeyonder(UUID uuid) {
        return beyonders.get(uuid);
    }

    public Beyonder createBeyonder(UUID uuid, Pathway pathway, int sequence, boolean addAbilities, boolean includePreviousSequences, boolean hostile) {
        LOTM.getLogUtil().info("Creating Beyonder with UUID " + uuid + " and Pathway " + (pathway == null ? "null" : pathway.getName()) + " and Sequence " + sequence);

        Entity entity = Bukkit.getEntity(uuid);
        boolean isOfflinePlayer = Bukkit.getOfflinePlayer(uuid).hasPlayedBefore();

        if(entity == null && !isOfflinePlayer) {
            LOTM.getLogUtil().info("Entity with UUID " + uuid + " not found.");
            return null;
        }

        boolean isPlayer = isOfflinePlayer || entity instanceof Player;

        if(!isPlayer) {
            Player nearestPlayer = getNearestPlayer(entity.getLocation());
            if(nearestPlayer == null || nearestPlayer.getLocation().distance(entity.getLocation()) > 200) return null;

            if(spawnedBeyonders.size() > 150) return null;
        }

        Beyonder beyonder = isPlayer ? new BeyonderPlayer(uuid, pathway, sequence) : new BeyonderEntity(uuid, pathway, sequence, hostile);

        if(beyonder instanceof BeyonderEntity) {
            spawnedBeyonders.add(beyonder);
        }

        if(addAbilities && pathway != null) {
            pathway.setSequencePathway(beyonder, sequence, includePreviousSequences);
        }

        beyonders.put(uuid, beyonder);

        return beyonder;
    }

    public BeyonderSpirit createBeyonderSpirit(UUID uuid, Pathway pathway, int sequence, boolean addAbilities, boolean includePreviousSequences, boolean hostile) {
        LOTM.getLogUtil().info("Creating Beyonder with UUID " + uuid + " and Pathway " + (pathway == null ? "null" : pathway.getName()) + " and Sequence " + sequence);

        Entity entity = Bukkit.getEntity(uuid);

        if(entity == null) {
            LOTM.getLogUtil().info("Entity with UUID " + uuid + " not found.");
            return null;
        }

        Player nearestPlayer = getNearestPlayer(entity.getLocation());
        if(nearestPlayer == null || nearestPlayer.getLocation().distance(entity.getLocation()) > 200) return null;

        if(spawnedBeyonders.size() > 150) return null;

        BeyonderSpirit beyonder = new BeyonderSpirit(uuid, pathway, sequence, hostile, 20 * 45);

        if(addAbilities && pathway != null) {
            pathway.setSequencePathway(beyonder, sequence, includePreviousSequences);
        }

        beyonders.put(uuid, beyonder);

        return beyonder;
    }

    public BeyonderNPC createBeyonderNPC(@NonNull Pathway pathway, int sequence, boolean hostile, Location location, boolean dummy) {
        if(!isCitizensEnabled)
            return null;

        Player nearestPlayer = getNearestPlayer(location);
        if(nearestPlayer == null || nearestPlayer.getLocation().distance(location) > 200) return null;

        if(spawnedBeyonders.size() > 150) return null;

        LOTM.getLogUtil().info("Creating Beyonder NPC with Pathway " + pathway.getName() + " and Sequence " + sequence);
        BeyonderNPC beyonder = new BeyonderNPC(location, pathway, sequence, hostile, dummy);
        pathway.setSequencePathway(beyonder, sequence, true);
        beyonders.put(beyonder.getUuid(), beyonder);

        spawnedBeyonders.add(beyonder);
        return beyonder;
    }

    public BeyonderNPC createBeyonderNPC(@NonNull Pathway pathway, int sequence, boolean hostile, Location location) {
        return createBeyonderNPC(pathway, sequence, hostile, location, false);
    }

    public Beyonder createBeyonder(UUID uuid, Pathway pathway, int sequence, boolean addAbilities, boolean includePreviousSequences) {
        return createBeyonder(uuid, pathway, sequence, addAbilities, includePreviousSequences, false);
    }


    public Beyonder createBeyonder(UUID uuid, Pathway pathway, int sequence) {
        return createBeyonder(uuid, pathway, sequence, true, true, false);
    }

    public BeyonderSpirit createBeyonderSpirit(UUID uuid, Pathway pathway, int sequence, boolean addAbilities, boolean includePreviousSequences) {
        return createBeyonderSpirit(uuid, pathway, sequence, addAbilities, includePreviousSequences, false);
    }


    public BeyonderSpirit createBeyonderSpirit(UUID uuid, Pathway pathway, int sequence) {
        return createBeyonderSpirit(uuid, pathway, sequence, true, true, false);
    }

    public void removeBeyonder(UUID uuid) {
        beyonders.remove(uuid);
    }

    public HashMap<UUID, Beyonder> getBeyonders() {
        return new HashMap<>(beyonders);
    }

    @SuppressWarnings("all")
    private void registerCommands() {
        getCommand("beyonder").setExecutor(new BeyonderCommand());
        getCommand("beyonder").setTabCompleter(new BeyonderCommandTabCompleter());

        getCommand("potion").setExecutor(new PotionCommand());

        getCommand("beyonderentity").setExecutor(new BeyonderEntityCommand());
        getCommand("beyonderentity").setTabCompleter(new BeyonderEntityCommandTabCompleter());

        getCommand("entity").setExecutor(new EntityCommand());
        getCommand("entity").setTabCompleter(new EntityCommandTabCompleter());

        getCommand("spirit").setExecutor(new SpiritCommand());
        getCommand("spirit").setTabCompleter(new SpiritCommandTabCompleter());

        getCommand("abilities").setExecutor(new AbilitiesCommand());
        getCommand("toggleGriefing").setExecutor(new ToggleGriefingCommand());
        getCommand("refreshSpirituality").setExecutor(new RefreshSpiritualityCommand());

        getCommand("beyonderhelp").setExecutor(new BeyonderHelpCommand());
        getCommand("abilityinfos").setExecutor(new AbilityInfosCommand());
        getCommand("book").setExecutor(new BookCommand());
    }

    private void registerPathways() {
        pathways.put("red_priest", new RedPriestPathway("Red Priest", "red_priest", "4", Color.RED));
        pathways.put("door", new DoorPathway("Door", "door", "b", Color.AQUA));
        pathways.put("tyrant", new TyrantPathway("Tyrant", "tyrant", "9", Color.BLUE));
        pathways.put("twilight_giant", new TwilightGiantPathway("Twilight Giant", "twilight_giant", "6", Color.fromRGB(218, 145, 0)));
        pathways.put("abyss", new AbyssPathway("Abyss", "abyss", "4", Color.fromRGB(125, 10, 27)));
        pathways.put("moon", new MoonPathway("Moon", "moon", "c", Color.fromRGB(222, 29, 80)));
        pathways.put("death", new DeathPathway("Death", "death", "8", Color.fromRGB(52, 50, 54)));
        pathways.put("sun", new SunPathway("Sun", "sun", "6", Color.fromRGB(255, 191, 71)));
        pathways.put("hermit", new HermitPathway("Hermit", "hermit", "d", Color.fromRGB(176, 81, 240)));
    }

    @Override
    public void onDisable() {
        for(Beyonder beyonder : beyonders.values()) {
            if(!(beyonder instanceof BeyonderNPC beyonderNPC))
                continue;
            beyonderNPC.getNpc().destroy();
            CitizensAPI.getNPCRegistry().deregister(beyonderNPC.getNpc());
            CitizensAPI.getNPCRegistry().saveToStore();
        }

        // Save Beyonders
        BeyonderConfigManager.saveBeyonders();

        for(Entity entity : entitiesToRemove) {
            if(entity == null || !entity.isValid())
                continue;
            entity.remove();
        }

        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities())
                if(entity.getScoreboardTags().contains("spirit"))
                    entity.remove();
        }

        for(UUID uuid : previousGameModes.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                player.setGameMode(previousGameModes.get(uuid));
            }
        }

        for(Location location : blocksToRemove) {
            location.getBlock().setType(Material.AIR);
        }

        for(Beyonder beyonder : removeAllowFlight) {
            if(beyonder instanceof BeyonderPlayer) {
                Player player = ((BeyonderPlayer) beyonder).getPlayer();
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }

        for(Beyonder beyonder : removeInvisible) {
            beyonder.getEntity().setInvisible(false);
        }

        for (String tag : scoreboardTagsToRemove) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getScoreboardTags().removeIf(s -> s.startsWith(tag));
            }
            for(World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    entity.getScoreboardTags().removeIf(s -> s.startsWith(tag));
                }
            }
        }

        for(Beyonder beyonder : resetScale) {
            if(beyonder instanceof BeyonderPlayer) {
                Player player = ((BeyonderPlayer) beyonder).getPlayer();
                AttributeInstance attribute = player.getAttribute(Attribute.SCALE);
                if(attribute != null)
                    attribute.setBaseValue(1);
            }
        }

        logger.info("Disabled Plugin");
    }

    public Player getNearestPlayer(Location location) {
        Player currentPlayer = null;
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld() != location.getWorld()) continue;
            if(currentPlayer == null) currentPlayer = player;
            else if(currentPlayer.getLocation().distance(location) > player.getLocation().distance(location)) currentPlayer = player;
        }

        return currentPlayer;
    }

    public static LogUtil getLogUtil() {
        return logger;
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, instance);
    }

    public Pathway getPathway(String name) {
        return pathways.get(name);
    }

    public List<String> getPathwayKeys() {
        return List.copyOf(pathways.keySet());
    }

    public List<Pathway> getPathways() {
        return List.copyOf(pathways.values());
    }

}
