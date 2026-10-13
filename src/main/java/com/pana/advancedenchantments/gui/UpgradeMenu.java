package com.pana.advancedenchantments.gui;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import com.pana.advancedenchantments.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class UpgradeMenu implements InventoryHolder {

    private final CustomItemRegistry itemRegistry;
    private Inventory inventory;
    private MenuType currentType;
    private MenuType previousType;
    private ItemStack cornerGlass;

    public enum MenuType {
        MAIN,
        MATERIALS,
        TEMPLATES,
        CRAFTING_VIEW
    }

    public UpgradeMenu(CustomItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    public MenuType getCurrentType() {
        return currentType;
    }

    public MenuType getPreviousType() {
        return previousType;
    }

    public void setPreviousType(MenuType menuType) {
        this.previousType = menuType;
    }

    public Integer getBackSlot(MenuType currentType){
        switch (currentType){
            case MATERIALS -> {
                return 49;
            }
            case TEMPLATES, CRAFTING_VIEW -> {
                return 40;
            }
        }
        return -1;
    }

    // 1era Interfaz: Menú Principal (/e upgrades)
    public void openMainMenu(Player player) {
        this.currentType = MenuType.MAIN;
        this.inventory = Bukkit.createInventory(this, 27,
                MiniMessage.miniMessage().deserialize("<b><gradient:#FFA300:#D00000>ʀᴇᴄᴇᴛᴀs ᴅᴇ ᴍᴇᴊᴏʀᴀs</gradient></b>")
        );

        // Slot 11: Lingote de Hierro (Materiales)
        ItemStack materialsItem = itemRegistry.createFieryIron();
        ItemMeta matMeta = materialsItem.getItemMeta();
        if (matMeta != null) {
            matMeta.displayName(MiniMessage.miniMessage().deserialize("<!italic><yellow><bold>Materiales</bold></yellow>"));
            matMeta.lore(List.of(
                    MiniMessage.miniMessage().deserialize("<!italic><gray>Sirven para craftear plantillas custom.</gray>"),
                    MiniMessage.miniMessage().deserialize("<!italic><dark_gray>Haz clic para ver los materiales.</dark_gray>")
            ));
            materialsItem.setItemMeta(matMeta);
        }
        inventory.setItem(11, materialsItem);

        // Slot 15: Plantilla Base
        ItemStack templateItem = itemRegistry.createTemplate(CustomItemType.TEMPLATE_COMMON);
        ItemMeta tempMeta = templateItem.getItemMeta();
        if (tempMeta != null) {
            tempMeta.displayName(MiniMessage.miniMessage().deserialize("<!italic><aqua><bold>Plantillas de Mejora</bold></aqua>"));
            tempMeta.lore(List.of(
                    MiniMessage.miniMessage().deserialize("<!italic><gray>Sirven para subir de nivel un encantamiento custom.</gray>"),
                    MiniMessage.miniMessage().deserialize("<!italic><dark_gray>Haz clic para ver las plantillas.</dark_gray>")
            ));

            //Template, hide Applies to: & Ingredients:

            templateItem.setItemMeta(tempMeta);
        }
        inventory.setItem(15, templateItem);

        setCornerGlass();
        inventory.setItem(0, cornerGlass);
        inventory.setItem(8, cornerGlass);
        inventory.setItem(18, cornerGlass);
        inventory.setItem(26, cornerGlass);

        player.openInventory(inventory);
    }

    // 2da Interfaz: Materiales Base
    public void openMaterialsMenu(Player player) {
        this.currentType = MenuType.MATERIALS;
        this.inventory = Bukkit.createInventory(this, 54,
                MiniMessage.miniMessage().deserialize("<b><gradient:#6E6E6E:#151515>ᴍᴀᴛᴇʀɪᴀʟᴇs ʙᴀsᴇ</gradient></b>")
        );

        // Aquí listamos algunos o todos los materiales custom relevantes
        inventory.setItem(10, itemRegistry.createFieryIron());

        inventory.setItem(11, itemRegistry.createGoldFieryCompact());
        inventory.setItem(20, itemRegistry.createGoldAiryCompact());

        inventory.setItem(12, itemRegistry.createGeodeFieryCompact());
        inventory.setItem(21, itemRegistry.createGeodeAiryCompact());
        inventory.setItem(30, itemRegistry.createGeodeCarrotCompact());

        inventory.setItem(13, itemRegistry.createRedluziCompact());

        inventory.setItem(14, itemRegistry.createRedluziFieryCompact());
        inventory.setItem(23, itemRegistry.createRedluziFieryCompact());
        inventory.setItem(32, itemRegistry.createRedluziFieryCompact());
        inventory.setItem(41, itemRegistry.createRedluziFieryCompact());

        inventory.setItem(15, itemRegistry.createEmeraldAiryCompact());
        inventory.setItem(24, itemRegistry.createEmeraldCarrotCompact());
        inventory.setItem(33, itemRegistry.createEmeraldAquaCompact());
        inventory.setItem(42, itemRegistry.createEmeraldDeathCompact());

        inventory.setItem(16, itemRegistry.createDiamondCarrotCompact());
        inventory.setItem(25, itemRegistry.createDiamondAquaCompact());
        inventory.setItem(34, itemRegistry.createDiamondDeathCompact());
        inventory.setItem(43, itemRegistry.createDiamondHornCompact());

        addBackButton();

        setCornerGlass();
        inventory.setItem(0, cornerGlass);
        inventory.setItem(8, cornerGlass);
        inventory.setItem(45, cornerGlass);
        inventory.setItem(53, cornerGlass);

        player.openInventory(inventory);
    }

    // 2da Interfaz: Plantillas de Mejora (con las posiciones exactas solicitadas)
    public void openTemplatesMenu(Player player) {
        this.currentType = MenuType.TEMPLATES;
        this.inventory = Bukkit.createInventory(this, 45,
                MiniMessage.miniMessage().deserialize("<b><gradient:#6E6E6E:#151515>ᴘʟᴀɴᴛɪʟʟᴀs ᴅᴇ ᴍᴇᴊᴏʀᴀ</gradient></b>")
        );

        inventory.setItem(11, itemRegistry.createTemplate(CustomItemType.TEMPLATE_COMMON));
        inventory.setItem(15, itemRegistry.createTemplate(CustomItemType.TEMPLATE_MYTHIC));
        inventory.setItem(19, itemRegistry.createTemplate(CustomItemType.TEMPLATE_RARE));
        inventory.setItem(25, itemRegistry.createTemplate(CustomItemType.TEMPLATE_LEGENDARY));
        inventory.setItem(29, itemRegistry.createTemplate(CustomItemType.TEMPLATE_EPIC));
        inventory.setItem(33, itemRegistry.createTemplate(CustomItemType.TEMPLATE_ABSOLUTE));

        addBackButton();

        setCornerGlass();
        inventory.setItem(0, cornerGlass);
        inventory.setItem(8, cornerGlass);
        inventory.setItem(36, cornerGlass);
        inventory.setItem(44, cornerGlass);

        player.openInventory(inventory);
    }

    // 3era Interfaz: Vista estática de receta de crafteo
    public void openCraftingView(Player player, ShapedRecipe recipe) {
        this.currentType = MenuType.CRAFTING_VIEW;
        this.inventory = Bukkit.createInventory(this, 45,
                MiniMessage.miniMessage().deserialize("<b><gradient:#6E6E6E:#151515>ʀᴇᴄᴇᴛᴀ</gradient></b>")
        );

        // 1. Colocar el ítem resultante de la receta en un slot lateral visible (ej. el slot 24)
        inventory.setItem(24, recipe.getResult());

        // 2. Mapear los slots de la cuadrícula 3x3 del menú
        int[][] gridSlots = {
                {10, 11, 12},
                {19, 20, 21},
                {28, 29, 30}
        };

        // 3. Extraer la forma (shape) y los ingredientes de la receta
        String[] shape = recipe.getShape();
        Map<Character, RecipeChoice> choiceMap = recipe.getChoiceMap();

        for (int row = 0; row < shape.length; row++) {
            String rowString = shape[row];
            for (int col = 0; col < rowString.length(); col++) {
                char c = rowString.charAt(col);
                RecipeChoice choice = choiceMap.get(c);

                if (choice != null) {
                    ItemStack ingredientItem = getItemStack(choice);

                    // Si encontramos un ítem válido y no es AIRE
                    if (ingredientItem != null && !ingredientItem.getType().isAir()) {
                        int targetSlot = gridSlots[row][col];
                        inventory.setItem(targetSlot, ingredientItem);
                    }
                }
            }
        }

        addBackButton();

        setCornerGlass();
        inventory.setItem(0, cornerGlass);
        inventory.setItem(8, cornerGlass);
        inventory.setItem(36, cornerGlass);
        inventory.setItem(44, cornerGlass);

        player.openInventory(inventory);
    }

    private static @Nullable ItemStack getItemStack(RecipeChoice choice) {
        ItemStack ingredientItem = null;

        if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            // Si es ExactChoice, tomamos el primer ítem exacto
            if (!exactChoice.getChoices().isEmpty()) {
                ingredientItem = exactChoice.getChoices().getFirst();
            }
        } else if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            // Si es un MaterialChoice (ej. madera vanilla, tintes, etc.), tomamos el primer material
            if (!materialChoice.getChoices().isEmpty()) {
                ingredientItem = new ItemStack(materialChoice.getChoices().getFirst());
            }
        }
        return ingredientItem;
    }

    public void addBackButton(){
        Integer slot = getBackSlot(currentType);
        if(slot >= 0){
            ItemStack item = new ItemStack(Material.ARROW);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(MiniMessage.miniMessage().deserialize("<!italic><b><gradient:#F5F3B1:#FFF818>ᴠᴏʟᴠᴇʀ</gradient></b>"));
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        }
    }

    public void setCornerGlass() {
        ItemStack cornerGlass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta cornerGlassMeta = cornerGlass.getItemMeta();

        // Ocultar efectos de encantamiento y atributos para evitar el brillo violeta
        cornerGlassMeta.setHideTooltip(true);

        cornerGlass.setItemMeta(cornerGlassMeta);
        this.cornerGlass = cornerGlass;
    }
}