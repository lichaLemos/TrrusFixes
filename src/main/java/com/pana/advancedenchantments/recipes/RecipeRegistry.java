package com.pana.advancedenchantments.recipes;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class RecipeRegistry {

    private final JavaPlugin plugin;
    private final CustomItemRegistry itemRegistry;
    private final IngredientRecipes ingredientRecipes;
    private final TemplateRecipes templateRecipes;

    public RecipeRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.itemRegistry = new CustomItemRegistry(plugin);
        this.ingredientRecipes = new IngredientRecipes(plugin, itemRegistry);
        this.templateRecipes = new TemplateRecipes(plugin, itemRegistry);
    }

    public void registerAll() {
        removeExistingRecipes();
        ingredientRecipes.register();
        templateRecipes.registerAll();
    }

    private void removeExistingRecipes() {
        // Alternativa limpia y directa para eliminar las keys específicas de tu plugin:
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_fiery_iron"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_template_common"));

        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_gold_fiery_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_gold_airy_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_template_rare"));

        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_geode_carrot_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_geode_fiery_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_geode_airy_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_template_epic"));

        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_redluzi_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_redluzi_fiery_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_redluzi_airy_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_redluzi_carrot_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_redluzi_aqua_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_template_mythic"));

        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_emerald_airy_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_emerald_carrot_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_emerald_aqua_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_emerald_death_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_template_legendary"));

        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_diamond_carrot_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_diamond_aqua_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_diamond_death_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_diamond_horn_compact"));
        Bukkit.removeRecipe(new NamespacedKey(plugin, "craft_template_absolute"));
    }

    public CustomItemRegistry getItemRegistry() {
        return itemRegistry;
    }
}