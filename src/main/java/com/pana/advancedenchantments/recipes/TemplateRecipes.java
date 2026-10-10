package com.pana.advancedenchantments.recipes;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import com.pana.advancedenchantments.items.CustomItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class TemplateRecipes {

    private final JavaPlugin plugin;
    private final CustomItemRegistry items;

    public TemplateRecipes(JavaPlugin plugin, CustomItemRegistry items) {
        this.plugin = plugin;
        this.items = items;
    }

    public void registerAll() {
        registerCommonTemplate();
        registerRareTemplate();
        registerEpicTemplate();
        registerMythicTemplate();
        registerLegendaryTemplate();
        registerAbsoluteTemplate();
    }

    // 1. PLANTILLA COMÚN (Textura VEX)
    private void registerCommonTemplate() {
        ItemStack result = items.createTemplate(CustomItemType.TEMPLATE_COMMON);
        NamespacedKey key = new NamespacedKey(plugin, "craft_template_common");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                " F ",
                "FTF",
                " F "
        );

        recipe.setIngredient('F', new RecipeChoice.ExactChoice(items.createFieryIron()));
        recipe.setIngredient('T', Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);

        Bukkit.addRecipe(recipe);
    }

    // 2. PLANTILLA RARA (Textura FLOW)
    private void registerRareTemplate() {
        ItemStack result = items.createTemplate(CustomItemType.TEMPLATE_RARE);
        NamespacedKey key = new NamespacedKey(plugin, "craft_template_rare");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                " F ",
                "ATA",
                " F "
        );

        recipe.setIngredient('F', new RecipeChoice.ExactChoice(items.createGoldFieryCompact()));
        recipe.setIngredient('A', new RecipeChoice.ExactChoice(items.createGoldAiryCompact()));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(items.createTemplate(CustomItemType.TEMPLATE_COMMON)));

        Bukkit.addRecipe(recipe);
    }

    // 3. PLANTILLA ÉPICA (Textura SPIRE)
    private void registerEpicTemplate() {
        ItemStack result = items.createTemplate(CustomItemType.TEMPLATE_EPIC);
        NamespacedKey key = new NamespacedKey(plugin, "craft_template_epic");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                " F ",
                "CTC",
                " A "
        );

        recipe.setIngredient('F', new RecipeChoice.ExactChoice(items.createGeodeFieryCompact()));
        recipe.setIngredient('A', new RecipeChoice.ExactChoice(items.createGeodeAiryCompact()));
        recipe.setIngredient('C', new RecipeChoice.ExactChoice(items.createGeodeCarrotCompact()));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(items.createTemplate(CustomItemType.TEMPLATE_RARE)));

        Bukkit.addRecipe(recipe);
    }

    // 4. PLANTILLA MÍTICA (Textura RIB)
    private void registerMythicTemplate() {
        ItemStack result = items.createTemplate(CustomItemType.TEMPLATE_MYTHIC);
        NamespacedKey key = new NamespacedKey(plugin, "craft_template_mythic");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                " F ",
                "CTQ",
                " A "
        );

        recipe.setIngredient('F', new RecipeChoice.ExactChoice(items.createRedluziFieryCompact()));
        recipe.setIngredient('A', new RecipeChoice.ExactChoice(items.createRedluziAiryCompact()));
        recipe.setIngredient('C', new RecipeChoice.ExactChoice(items.createRedluziCarrotCompact()));
        recipe.setIngredient('Q', new RecipeChoice.ExactChoice(items.createRedluziAquaCompact()));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(items.createTemplate(CustomItemType.TEMPLATE_EPIC)));

        Bukkit.addRecipe(recipe);
    }

    // 5. PLANTILLA LEGENDARIA (Textura EYE)
    private void registerLegendaryTemplate() {
        ItemStack result = items.createTemplate(CustomItemType.TEMPLATE_LEGENDARY);
        NamespacedKey key = new NamespacedKey(plugin, "craft_template_legendary");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                " D ",
                "CTQ",
                " A "
        );

        recipe.setIngredient('A', new RecipeChoice.ExactChoice(items.createEmeraldAiryCompact()));
        recipe.setIngredient('C', new RecipeChoice.ExactChoice(items.createEmeraldCarrotCompact()));
        recipe.setIngredient('Q', new RecipeChoice.ExactChoice(items.createEmeraldAquaCompact()));
        recipe.setIngredient('D', new RecipeChoice.ExactChoice(items.createEmeraldDeathCompact()));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(items.createTemplate(CustomItemType.TEMPLATE_MYTHIC)));

        Bukkit.addRecipe(recipe);
    }

    // 6. PLANTILLA ABSOLUTA (Textura TIDE)
    private void registerAbsoluteTemplate() {
        ItemStack result = items.createTemplate(CustomItemType.TEMPLATE_ABSOLUTE);
        NamespacedKey key = new NamespacedKey(plugin, "craft_template_absolute");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(
                " D ",
                "CTQ",
                " H "
        );

        recipe.setIngredient('C', new RecipeChoice.ExactChoice(items.createDiamondCarrotCompact()));
        recipe.setIngredient('Q', new RecipeChoice.ExactChoice(items.createDiamondAquaCompact()));
        recipe.setIngredient('D', new RecipeChoice.ExactChoice(items.createDiamondDeathCompact()));
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(items.createDiamondHornCompact()));
        recipe.setIngredient('T', new RecipeChoice.ExactChoice(items.createTemplate(CustomItemType.TEMPLATE_LEGENDARY)));

        Bukkit.addRecipe(recipe);
    }
}