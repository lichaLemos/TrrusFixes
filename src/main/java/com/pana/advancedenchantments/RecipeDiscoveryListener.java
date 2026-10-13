package com.pana.advancedenchantments;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeDiscoveryListener implements Listener {

    private final String pluginNamespace;

    public RecipeDiscoveryListener(String pluginNamespace) {
        this.pluginNamespace = pluginNamespace;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<NamespacedKey> customRecipeKeys = new ArrayList<>();

        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            // Verificamos si la receta implementa org.bukkit.Keyed
            if (recipe instanceof Keyed keyedRecipe) {
                NamespacedKey key = keyedRecipe.getKey();
                if (key.getNamespace().equalsIgnoreCase(pluginNamespace)) {
                    customRecipeKeys.add(key);
                }
            }
        }

        if (!customRecipeKeys.isEmpty()) {
            player.discoverRecipes(customRecipeKeys);
        }
    }
}