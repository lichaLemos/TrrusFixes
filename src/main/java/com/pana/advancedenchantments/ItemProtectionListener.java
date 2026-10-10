package com.pana.advancedenchantments;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Crafter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemProtectionListener implements Listener {

    private final CustomItemRegistry itemRegistry;

    public ItemProtectionListener(CustomItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    private boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(itemRegistry.getItemKey(), PersistentDataType.STRING);
    }

    // 1. Evitar colocar bloques personalizados (como el Redluzi)
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (isCustomItem(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes colocar este item como bloque."));
        }
    }

    // 2. Evitar crafteos en mesas de crafteo vainilla (crear bloques o herramientas con tus ítems)
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (isCustomItem(item)) {
                event.getInventory().setResult(null);
                break;
            }
        }
    }

    // 3. Interceptar la activación física del bloque Crafter por Redstone
    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent event) {
        // Obtenemos el inventario del bloque Crafter
        if (event.getBlock().getState() instanceof Crafter crafter) {
            org.bukkit.inventory.Inventory inventory = crafter.getInventory();

            boolean containsCustom = false;

            // Recorremos las 9 casillas del inventario del Crafter
            for (ItemStack item : inventory.getContents()) {
                if (isCustomItem(item)) {
                    containsCustom = true;
                    break;
                }
            }

            // Si la grilla tiene al menos un ítem personalizado nuestro...
            if (containsCustom) {
                // Obtenemos el resultado que la receta va a generar
                ItemStack result = event.getResult();

                // Si el resultado NO es un ítem personalizado nuestro (ej. intenta hacer un bloque vanilla), cancelamos
                if (!isCustomItem(result)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // 4. Controlar clics, Shift+Click y protección en interfaces (Aldeanos, Mesas, etc.)
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryType invType = event.getView().getTopInventory().getType();

        // Detectar interfaces restringidas por tipo nativo de Bukkit/Paper
        boolean isRestrictedInterface = invType == InventoryType.MERCHANT ||
                invType == InventoryType.SMITHING ||
                invType == InventoryType.ANVIL ||
                invType == InventoryType.CARTOGRAPHY ||
                invType == InventoryType.STONECUTTER ||
                invType == InventoryType.WORKBENCH;

        if (!isRestrictedInterface) return;

        int rawSlot = event.getRawSlot();
        int topInventorySize = event.getView().getTopInventory().getSize();
        Player player = (Player) event.getWhoClicked();

        // CASO ESPECIAL ALDEANOS (MERCHANT)
        if (invType == InventoryType.MERCHANT) {
            org.bukkit.inventory.MerchantInventory merchantInv = (org.bukkit.inventory.MerchantInventory) event.getView().getTopInventory();
            ItemStack item1 = merchantInv.getItem(0);
            ItemStack item2 = merchantInv.getItem(1);

            if (isCustomItem(item1) || isCustomItem(item2)) {
                if (rawSlot == 2 || event.isShiftClick() || rawSlot < topInventorySize) {
                    event.setCancelled(true);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes usar objetos personalizados en los intercambios de aldeanos."));
                    return;
                }
            }
        }

        // CASO A: Clic normal dentro de la interfaz superior
        if (rawSlot < topInventorySize) {
            ItemStack cursorItem = event.getCursor();
            ItemStack currentItem = event.getCurrentItem();

            if (isCustomItem(cursorItem) || isCustomItem(currentItem)) {
                event.setCancelled(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes usar este objeto en esta máquina o interfaz."));
                return;
            }
        }

        // CASO B: Shift + Click desde el inventario inferior hacia la máquina/interfaz
        if (event.isShiftClick()) {
            ItemStack clickedItem = event.getCurrentItem();
            if (isCustomItem(clickedItem)) {
                event.setCancelled(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes mover este objeto hacia esta interfaz de automatización."));
            }
        }
    }
}