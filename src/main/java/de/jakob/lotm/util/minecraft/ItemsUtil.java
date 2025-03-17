package de.jakob.lotm.util.minecraft;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemsUtil {
    public static ItemStack getNameLessItem(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null)
            return item;
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCustomHead(String textureUrl) {
        // Create a new ItemStack of type PLAYER_HEAD
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        // Get the meta and check if it is an instance of SkullMeta
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        if (skullMeta != null) {
            // Generate a random UUID for the PlayerProfile
            UUID uuid = UUID.randomUUID();
            // Create a PlayerProfile with the generated UUID
            PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
            // Retrieve the PlayerTextures from the profile
            PlayerTextures textures = profile.getTextures();
            try {
                // Set the skin texture URL
                textures.setSkin(new URL(textureUrl));
            } catch (MalformedURLException e) {
                return head; // Return unmodified head if the URL is invalid
            }
            // Apply the textures back to the profile
            profile.setTextures(textures);
            // Set the PlayerProfile to the SkullMeta
            skullMeta.setOwnerProfile(profile);
            // Set the updated meta back to the ItemStack
            head.setItemMeta(skullMeta);
        }

        return head;
    }

    public static ItemStack redWine() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if(potionMeta != null) {
            potionMeta.setColor(Color.RED);
            potionMeta.setDisplayName("§r§cRed Wine");
            potionMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        }

        item.setItemMeta(potionMeta);
        return item;
    }

    public static ItemStack drownedLung() {
        ItemStack item = new ItemStack(Material.GLOW_INK_SAC);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§9Drowned Lung");
            addTextureComponent("drowned_lung", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack blueFeather() {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§9Blue Feather");
            addTextureComponent("blue_feather", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack captainsLogBook() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§9Captain's Logbook");
            addTextureComponent("logbook", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack songCrystal() {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§9Elder Guardian's Song Crystal");
            addTextureComponent("song_crystal", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack phantomWing() {
        ItemStack item = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§dPhantom Wing");
            addTextureComponent("phantom_wings", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack squidBlood() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§cLavos Squid's Crystallized blood");
            addTextureComponent("squid_blood", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack wraithArtifact() {
        ItemStack item = new ItemStack(Material.ANGLER_POTTERY_SHERD);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§dArtifact from an ancient wraith");
            addTextureComponent("wraith_artifact", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack echoPearl() {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§dEcho Pearl");
            addTextureComponent("echo_pearl", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack wolfFang() {
        ItemStack item = new ItemStack(Material.QUARTZ);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§4Wolf Fang");
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack wolfBlood() {
        ItemStack item = new ItemStack(Material.LINGERING_POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§4Gray Demonic Wolf's Blood");
            meta.setColor(Color.fromRGB(255, 0, 0));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void addTextureComponent(String id, ItemMeta meta) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        List<String> tags = new ArrayList<>(component.getStrings());
        tags.add(id);
        component.setStrings(tags);

        meta.setCustomModelDataComponent(component);
    }

    public static ItemStack wolfClaws() {
        ItemStack item = new ItemStack(Material.ARMADILLO_SCUTE);
        ItemMeta meta =  item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§7Gray Demonic Wolf's Front Claws");
            addTextureComponent("demonic_wolf_claws", meta);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack boarTusk() {
        ItemStack item = new ItemStack(Material.GOAT_HORN);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§4Wild Boar Tusk");
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack salamanderGland() {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§4Fire Salamander Gland");
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getInfoBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("§5✦ Guide to Beyonders ✦§r");
        meta.setAuthor("Mysterious Scholar");

        // Collect pathways for second page
        List<Pathway> pathways = LOTM.getInstance().getPathways();
        StringBuilder pathwaysList = new StringBuilder();
        for (Pathway pathway : pathways) {
            pathwaysList.append("§r- ").append(pathway.getColorPrefix()).append("§l").append(pathway.getDisplayName()).append("§r\n");
        }

        // Introduction Page
        meta.addPage("§5§l✦ Guide to Beyonders ✦\n§r" +
                "In this world, there are many pathways one can take to ascend.\n\n" +
                "Currently, there are §d§l" + pathways.size() + " implemented pathways§r.\n" +
                "However, only §d§lSequences 9 to 4§r are currently available.");

        // Pathways Page
        meta.addPage("§3§l✦ Available Pathways ✦§r\n\n" + pathwaysList);

        // Commands Page
        meta.addPage("§3§l✦ Commands ✦\n§r" + "§6§l/beyonder (op only)§r - Become a Beyonder.\n" + "§6§l/beyonderhelp§r - Get relevant information.\n" + "§6§l/book§r - Get this book.\n" + "§6§l/abilities§r - Access all your abilities.");

        // Rogue Beyonders Page
        meta.addPage("§3§l✦ Rogue Beyonders ✦\n§r" + "You may encounter rogue Beyonders in the world.\n" + "They can be §d§lhostile§r or §d§lpassive§r.\n\n" + "They drop their §6§lBeyonder Characteristics§r upon defeat.");

        // Potion System Page
        meta.addPage("§3§l✦ The Potion System ✦\n§r" + "To create a potion, first §9§lright-click a cauldron§r with a §9§lWater Bottle.§r\n\n" + "§lParticles§r will appear, indicating you can now brew.\n\n" + "You can brew using either a §6§lBeyonder Characteristic§r or a recipe.");

        // Brewing Methods Page
        meta.addPage("§3§l✦ Brewing Methods ✦\n§r" + "When using a §6§lBeyonder Characteristic,§r no other ingredients are needed.\n\n" + "When using a §d§lrecipe,§r the order of ingredients does not matter.\n\n" + "§9§lRight-click§r to add ingredients, §9§lleft-click§r to complete the potion.");

        // List all pathways and their recipes
        for (Pathway pathway : pathways) {
            StringBuilder pageContent = new StringBuilder(pathway.getColorPrefix() + "§l✦ " + pathway.getDisplayName() + " Recipes\n§r");
            boolean hasRecipes = false;

            for (int sequence = 9; sequence >= 4; sequence--) {
                if (pageContent.length() > 170) {
                    meta.addPage(pageContent.toString());
                    pageContent = new StringBuilder();
                }

                HashMap<Integer, ItemStack[]> recipes = pathway.getPotionIngredients();
                if (recipes.containsKey(sequence)) {
                    hasRecipes = true;
                    pageContent.append("§lSequence ").append(sequence).append("§r\n");
                    for (ItemStack item : recipes.get(sequence)) {
                        if (pageContent.length() > 170) {
                            meta.addPage(pageContent.toString());
                            pageContent = new StringBuilder();
                        }
                        ItemMeta itemMeta = item.getItemMeta();
                        String itemName = (itemMeta != null && itemMeta.hasDisplayName()) ? itemMeta.getDisplayName() : StringUtils.capitalize(item.getType().toString().replace("_", " "));
                        pageContent.append("- ").append(itemName).append("§r\n");
                    }

                    pageContent.append("\n");
                }
            }

            if (hasRecipes) {
                meta.addPage(pageContent.toString());
            }
        }

        meta.setPages(meta.getPages());
        book.setItemMeta(meta);
        return book;
    }

    public static ItemStack spiderEyes() {
        ItemStack item = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName("§8Black Hunting Spider Eyes");
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ShapelessRecipe redWineRecipe() {
        ItemStack result = redWine();
        NamespacedKey key = new NamespacedKey(LOTM.getInstance(), "red_wine");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(Material.POTION);
        recipe.addIngredient(Material.SWEET_BERRIES);

        return recipe;
    }

    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if(item1 == null || item2 == null)
            return false;
        if(item1.getType() != item2.getType())
            return false;

        if(item1.hasItemMeta() && item2.hasItemMeta()) {
            ItemMeta meta1 = item1.getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();

            assert meta1 != null;
            assert meta2 != null;

            if(meta1.hasDisplayName() && meta2.hasDisplayName() && !meta1.getDisplayName().equals(meta2.getDisplayName()))
                return false;
            if(meta1.hasLore() && meta2.hasLore() && !meta1.getLore().get(0).equals(meta2.getLore().get(0)))
                return false;
            if (meta1.hasCustomModelData() && meta2.hasCustomModelData()) {
                try {
                    // Check integer-based custom model data safely
                    int cmd1 = meta1.getCustomModelData();
                    int cmd2 = meta2.getCustomModelData();
                    if (cmd1 != cmd2) {
                        return false;
                    }
                } catch (IllegalStateException e) {
                    // Ignore and proceed to component-based comparison
                }

                // Compare component-based custom model data
                CustomModelDataComponent component1 = meta1.getCustomModelDataComponent();
                CustomModelDataComponent component2 = meta2.getCustomModelDataComponent();

                if (component1 != null && component2 != null) {
                    List<String> tags1 = new ArrayList<>(component1.getStrings());
                    List<String> tags2 = new ArrayList<>(component2.getStrings());

                    if (!tags1.equals(tags2)) {
                        return false;
                    }
                }
            }

        }


        return true;
    }
}
