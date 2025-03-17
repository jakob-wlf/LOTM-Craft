package de.jakob.lotm.util.lotm;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class Lookup {

    public static EntityType getRampagerEntityForPathway(Pathway pathway) {
        return switch (pathway.getName()) {
            case "door" -> EntityType.ENDERMAN;
            case "twilight_giant" -> EntityType.IRON_GOLEM;
            case "red_priest" -> EntityType.BLAZE;
            case "tyrant", "abyss" -> EntityType.WITHER_SKELETON;
            default -> EntityType.RAVAGER;
        };
    }

    public static boolean isNoNPCSpawnEntity(Entity entity) {
        return switch (entity.getType()) {
            case BAT, PARROT, AXOLOTL, CAVE_SPIDER, WOLF, ALLAY -> true;
            default -> false;
        };
    }

    public static Pathway getPathwayForEntity(EntityType entityType) {
        return switch (entityType) {
            case DROWNED, ELDER_GUARDIAN, GUARDIAN, POLAR_BEAR -> LOTM.getInstance().getPathway("tyrant");
            case BLAZE, HUSK, SKELETON -> LOTM.getInstance().getPathway("red_priest");
            case WITHER_SKELETON, SPIDER, ZOMBIE -> LOTM.getInstance().getPathway("abyss");
            case IRON_GOLEM, PILLAGER, VILLAGER -> LOTM.getInstance().getPathway("twilight_giant");
            case ENDERMAN, EVOKER, WITCH -> LOTM.getInstance().getPathway("door");
            default -> null;
        };
    }

    public static final Set<Material> GROUND_MATERIALS = EnumSet.of(
            Material.GRASS_BLOCK,
            Material.DIRT,
            Material.COARSE_DIRT,
            Material.PODZOL,
            Material.DIRT_PATH,
            Material.SAND,
            Material.RED_SAND,
            Material.GRAVEL,
            Material.CLAY,
            Material.SNOW_BLOCK,
            Material.MYCELIUM,
            Material.NETHERRACK,
            Material.SOUL_SAND,
            Material.SOUL_SOIL,
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.FARMLAND,
            Material.STONE,
            Material.COBBLESTONE,
            Material.MOSSY_COBBLESTONE,
            Material.ANDESITE,
            Material.POLISHED_ANDESITE,
            Material.DIORITE,
            Material.POLISHED_DIORITE,
            Material.GRANITE,
            Material.POLISHED_GRANITE,
            Material.SANDSTONE,
            Material.CHISELED_SANDSTONE,
            Material.CUT_SANDSTONE,
            Material.SMOOTH_SANDSTONE,
            Material.RED_SANDSTONE,
            Material.CHISELED_RED_SANDSTONE,
            Material.CUT_RED_SANDSTONE,
            Material.SMOOTH_RED_SANDSTONE,
            Material.BLACKSTONE,
            Material.POLISHED_BLACKSTONE,
            Material.POLISHED_BLACKSTONE_BRICKS,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.POLISHED_DEEPSLATE,
            Material.DEEPSLATE_BRICKS,
            Material.TUFF,
            Material.CALCITE,
            Material.DRIPSTONE_BLOCK,
            Material.MUD,
            Material.PACKED_MUD,
            Material.MUDDY_MANGROVE_ROOTS
    );
}
