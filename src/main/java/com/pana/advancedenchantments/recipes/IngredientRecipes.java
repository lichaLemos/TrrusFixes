package com.pana.advancedenchantments.recipes;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class IngredientRecipes {

    private final JavaPlugin plugin;
    private final CustomItemRegistry itemRegistry;

    public IngredientRecipes(JavaPlugin plugin, CustomItemRegistry itemRegistry) {
        this.plugin = plugin;
        this.itemRegistry = itemRegistry;
    }

    public void register() {
        registerFieryIron();

        registerGoldFieryCompact();
        registerGoldAiryCompact();

        registerGeodeFieryCompact();
        registerGeodeAiryCompact();
        registerGeodeCarrotCompact();

        registerRedluziCompact();
        registerRedluziFieryCompact();
        registerRedluziAiryCompact();
        registerRedluziCarrotCompact();
        registerRedluziAquaCompact();

        registerEmeraldAiryCompact();
        registerEmeraldCarrotCompact();
        registerEmeraldAquaCompact();
        registerEmeraldDeathCompact();

        registerDiamondCarrotCompact();
        registerDiamondAquaCompact();
        registerDiamondDeathCompact();
        registerDiamondHornCompact();
    }

    //COMMON
    private void registerFieryIron() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_fiery_iron");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createFieryIron());
        recipe.shape("III", "IBI", "III");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('B', itemRegistry.createBlazeEssence());
        Bukkit.addRecipe(recipe);
    }

    //RARE
    private void registerGoldFieryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_gold_fiery_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createGoldFieryCompact());
        recipe.shape("GBG", "GPG", "GGG");
        recipe.setIngredient('B', Material.GOLD_BLOCK);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('P', itemRegistry.createBlazeEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerGoldAiryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_gold_airy_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createGoldAiryCompact());
        recipe.shape("GBG", "GWG", "GGG");
        recipe.setIngredient('B', Material.GOLD_BLOCK);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('W', itemRegistry.createBreezeEssence());
        Bukkit.addRecipe(recipe);
    }

    //EPIC
    private void registerGeodeCarrotCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_geode_carrot_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createGeodeCarrotCompact());
        recipe.shape("AAA", "AGA", "AAA");
        recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        recipe.setIngredient('G', itemRegistry.createPiglinEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerGeodeFieryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_geode_fiery_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createGeodeFieryCompact());
        recipe.shape("AAA", "APA", "AAA");
        recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        recipe.setIngredient('P', itemRegistry.createBlazeEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerGeodeAiryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_geode_airy_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createGeodeAiryCompact());
        recipe.shape("AAA", "AWA", "AAA");
        recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        recipe.setIngredient('W', itemRegistry.createBreezeEssence());
        Bukkit.addRecipe(recipe);
    }

    //MYTHIC
    private void registerRedluziCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_redluzi_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createRedluziCompact());
        recipe.shape("RLR", "LDL", "RLR");
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        recipe.setIngredient('L', Material.LAPIS_BLOCK);
        recipe.setIngredient('D', Material.FIRE_CHARGE);
        Bukkit.addRecipe(recipe);
    }

    private void registerRedluziFieryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_redluzi_fiery_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createRedluziFieryCompact());
        recipe.shape(" R ", "RPR", " R ");
        recipe.setIngredient('R', itemRegistry.createRedluziCompact());
        recipe.setIngredient('P', itemRegistry.createBlazeEssence());

        Bukkit.addRecipe(recipe);
    }

    private void registerRedluziAiryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_redluzi_airy_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createRedluziAiryCompact());
        recipe.shape(" R ", "RWR", " R ");
        recipe.setIngredient('R', itemRegistry.createRedluziCompact());
        recipe.setIngredient('W', itemRegistry.createBreezeEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerRedluziCarrotCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_redluzi_carrot_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createRedluziCarrotCompact());
        recipe.shape(" R ", "RCR", " R ");
        recipe.setIngredient('R', itemRegistry.createRedluziCompact());
        recipe.setIngredient('C', itemRegistry.createPiglinEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerRedluziAquaCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_redluzi_aqua_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createRedluziAquaCompact());
        recipe.shape(" R ", "RPR", " R ");
        recipe.setIngredient('R', itemRegistry.createRedluziCompact());
        recipe.setIngredient('P', itemRegistry.createGuardianEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerEmeraldAiryCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_emerald_airy_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createEmeraldAiryCompact());
        recipe.shape("BBB", "BWB", "BBB");
        recipe.setIngredient('B', Material.EMERALD_BLOCK);
        recipe.setIngredient('W', itemRegistry.createBreezeEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerEmeraldCarrotCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_emerald_carrot_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createEmeraldCarrotCompact());
        recipe.shape("BBB", "BCB", "BBB");
        recipe.setIngredient('B', Material.EMERALD_BLOCK);
        recipe.setIngredient('C', itemRegistry.createPiglinEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerEmeraldAquaCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_emerald_aqua_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createEmeraldAquaCompact());
        recipe.shape("BBB", "BPB", "BBB");
        recipe.setIngredient('B', Material.EMERALD_BLOCK);
        recipe.setIngredient('P', itemRegistry.createGuardianEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerEmeraldDeathCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_emerald_death_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createEmeraldDeathCompact());
        recipe.shape("BBB", "BWB", "BBB");
        recipe.setIngredient('B', Material.EMERALD_BLOCK);
        recipe.setIngredient('W', itemRegistry.createWitherEssence());

        Bukkit.addRecipe(recipe);
    }

    private void registerDiamondCarrotCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_diamond_carrot_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createDiamondCarrotCompact());
        recipe.shape("BBB", "BCB", "BBB");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('C', itemRegistry.createPiglinEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerDiamondAquaCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_diamond_aqua_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createDiamondAquaCompact());
        recipe.shape("BBB", "BPB", "BBB");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('P', itemRegistry.createGuardianEssence());
        Bukkit.addRecipe(recipe);
    }

    private void registerDiamondDeathCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_diamond_death_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createDiamondDeathCompact());
        recipe.shape("BBB", "BWB", "BBB");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('W', itemRegistry.createWitherEssence());

        Bukkit.addRecipe(recipe);
    }

    private void registerDiamondHornCompact() {
        NamespacedKey key = new NamespacedKey(plugin, "craft_diamond_horn_compact");
        ShapedRecipe recipe = new ShapedRecipe(key, itemRegistry.createDiamondHornCompact());
        recipe.shape("BBB", "BEB", "BBB");
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('E', itemRegistry.createWardenEssence());
        Bukkit.addRecipe(recipe);
    }
}