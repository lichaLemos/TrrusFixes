package com.pana.advancedenchantments.gui;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import static com.pana.advancedenchantments.gui.UpgradeMenu.MenuType.MAIN;

public class MenuListener implements Listener {

    private final JavaPlugin plugin;
    private final CustomItemRegistry itemRegistry;

    public MenuListener(CustomItemRegistry itemRegistry, JavaPlugin plugin) {
        this.itemRegistry = itemRegistry;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof UpgradeMenu menu)) return;

        event.setCancelled(true);
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        switch (menu.getCurrentType()) {
            case MAIN -> {
                UpgradeMenu newMenu = new UpgradeMenu(itemRegistry);
                if (event.getSlot() == 11) {
                    newMenu.setPreviousType(MAIN);
                    newMenu.openMaterialsMenu(player);
                }
                else if (event.getSlot() == 15) {
                    newMenu.setPreviousType(MAIN);
                    newMenu.openTemplatesMenu(player);
                }
            }
            case MATERIALS, TEMPLATES -> {
                if(event.getSlot() == menu.getBackSlot(menu.getCurrentType())){
                    UpgradeMenu newMenu = new UpgradeMenu(itemRegistry);
                    switch (menu.getPreviousType()){
                        case MAIN, CRAFTING_VIEW -> newMenu.openMainMenu(player);
                    }
                }

                // 1. Extract the ID of PersistentDataContainer of the clicked item
                if (!clickedItem.hasItemMeta()) return;
                String customId = clickedItem.getItemMeta().getPersistentDataContainer()
                        .get(itemRegistry.getItemKey(), PersistentDataType.STRING);

                if (customId == null) return;

                // 2. Mapeamos el ID custom con la receta correspondiente que registraste en Bukkit
                String recipeKeyName = getRecipeKeyFromCustomId(customId);
                if (recipeKeyName != null) {
                    org.bukkit.inventory.Recipe recipe = Bukkit.getRecipe(new NamespacedKey(plugin, recipeKeyName));
                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        UpgradeMenu newMenu = new UpgradeMenu(itemRegistry);
                        newMenu.setPreviousType(menu.getCurrentType());
                        newMenu.openCraftingView(player, shapedRecipe);
                    }
                }
            }
            case CRAFTING_VIEW -> {
                if(event.getSlot() == menu.getBackSlot(menu.getCurrentType())){
                    UpgradeMenu newMenu = new UpgradeMenu(itemRegistry);
                    switch (menu.getPreviousType()){
                        case MAIN -> {
                            newMenu.setPreviousType(MAIN);
                            newMenu.openMainMenu(player);
                        }
                        case MATERIALS -> {
                            newMenu.setPreviousType(MAIN);
                            newMenu.openMaterialsMenu(player);
                        }
                        case TEMPLATES -> {
                            newMenu.setPreviousType(MAIN);
                            newMenu.openTemplatesMenu(player);
                        }
                    }
                }

                // Si es el resultado de la receta no abre nuevo menu
                if (event.getSlot() == 24) return;

                // 1. Extract the ID of PersistentDataContainer of the clicked item
                if (!clickedItem.hasItemMeta()) return;
                String customId = clickedItem.getItemMeta().getPersistentDataContainer()
                        .get(itemRegistry.getItemKey(), PersistentDataType.STRING);

                if (customId == null) return;

                // 2. Mapeamos el ID custom con la receta correspondiente que registraste en Bukkit
                String recipeKeyName = getRecipeKeyFromCustomId(customId);
                if (recipeKeyName != null) {
                    org.bukkit.inventory.Recipe recipe = Bukkit.getRecipe(new NamespacedKey(plugin, recipeKeyName));
                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        UpgradeMenu newMenu = new UpgradeMenu(itemRegistry);
                        newMenu.setPreviousType(MAIN);
                        newMenu.openCraftingView(player, shapedRecipe);
                    }
                }
                // Vista estática
            }
        }
    }

    // Auxiliar method to relate item ID with recipe key
    private String getRecipeKeyFromCustomId(String customId) {
        return switch (customId) {
            // --- MATERIALES ---
            case "fiery_iron" -> "craft_fiery_iron";
            case "gold_fiery_compact" -> "craft_gold_fiery_compact";
            case "gold_airy_compact" -> "craft_gold_airy_compact";
            case "geode_fiery_compact" -> "craft_geode_fiery_compact";
            case "geode_airy_compact" -> "craft_geode_airy_compact";
            case "geode_carrot_compact" -> "craft_geode_carrot_compact";
            case "redluzi_compact" -> "craft_redluzi_compact";
            case "redluzi_fiery_compact" -> "craft_redluzi_fiery_compact";
            case "redluzi_airy_compact" -> "craft_redluzi_airy_compact";
            case "redluzi_carrot_compact" -> "craft_redluzi_carrot_compact";
            case "redluzi_aqua_compact" -> "craft_redluzi_aqua_compact";
            case "emerald_airy_compact" -> "craft_emerald_airy_compact";
            case "emerald_carrot_compact" -> "craft_emerald_carrot_compact";
            case "emerald_aqua_compact" -> "craft_emerald_aqua_compact";
            case "emerald_death_compact" -> "craft_emerald_death_compact";
            case "diamond_carrot_compact" -> "craft_diamond_carrot_compact";
            case "diamond_aqua_compact" -> "craft_diamond_aqua_compact";
            case "diamond_death_compact" -> "craft_diamond_death_compact";
            case "diamond_horn_compact" -> "craft_diamond_horn_compact";

            // --- PLANTILLAS ---
            case "template_common" -> "craft_template_common";
            case "template_rare" -> "craft_template_rare";
            case "template_epic" -> "craft_template_epic";
            case "template_mythic" -> "craft_template_mythic";
            case "template_legendary" -> "craft_template_legendary";
            case "template_absolute" -> "craft_template_absolute";

            default -> null;
        };
    }
}