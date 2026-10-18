package com.pana.mechanics.pickspawner;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ItemProtection implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey customItemKey;
    private final Map<UUID, List<ItemStack>> savedDeathItems = new HashMap<>();

    public ItemProtection(JavaPlugin plugin) {
        this.plugin = plugin;
        this.customItemKey = new NamespacedKey(plugin, "custom_item_id");
    }

    /**
     * Verifica si un ItemStack es el PickSpawner personalizado.
     */
    private boolean isPickSpawner(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        String value = meta.getPersistentDataContainer().get(customItemKey, PersistentDataType.STRING);
        return "custom_item_pickspawner".equals(value);
    }

    /**
     * Determina si un inventario es seguro/permitido para mover el PickSpawner.
     * Solo se permite el propio inventario del jugador (Crafting/Player) y el EnderChest.
     */
    private boolean isAllowedInventory(Inventory inventory) {
        if (inventory == null) return false;
        InventoryType type = inventory.getType();
        return type == InventoryType.PLAYER ||
                type == InventoryType.ENDER_CHEST;
    }

    /**
     * Evita que el jugador tire el PickSpawner al suelo.
     * Si el inventario está lleno (por ejemplo, al cerrar el inventario con el pico en el cursor),
     * lo rescata de forma segura enviándolo al EnderChest o a la cola de guardado.
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (isPickSpawner(item)) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            // Ejecutamos en el siguiente tick para asegurar que el evento se cancele correctamente
            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                // Eliminar la entidad del suelo por seguridad si se llegó a spawnear
                if (event.getItemDrop().isValid()) {
                    event.getItemDrop().remove();
                }

                // Intentamos meterlo de nuevo al inventario
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
                if (!leftovers.isEmpty()) {
                    // Si el inventario está lleno, lo mandamos a su EnderChest
                    for (ItemStack leftover : leftovers.values()) {
                        Map<Integer, ItemStack> enderLeftovers = player.getEnderChest().addItem(leftover);
                        if (!enderLeftovers.isEmpty()) {
                            // Si el EnderChest también está lleno, lo guardamos en la cola de seguridad
                            for (ItemStack reallyLeftover : enderLeftovers.values()) {
                                savedDeathItems.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(reallyLeftover);
                                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                                        .deserialize("<red>¡Inventario y EnderChest llenos! Tu PickSpawner se guardó de forma segura. Libera espacio para recuperarlo."));
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Controla el movimiento del PickSpawner dentro de las interfaces.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        Inventory clickedInventory = event.getClickedInventory();

        // 1. Evitar tirar el PickSpawner haciendo clic fuera del inventario (RawSlot -999) con el pico en el cursor
        if (event.getRawSlot() == -999) {
            if (isPickSpawner(event.getCursor())) {
                event.setCancelled(true);
                return;
            }
        }

        // Si no hay inventario clickeado, no hacemos nada
        if (clickedInventory == null) return;

        boolean topIsAllowed = isAllowedInventory(topInventory);

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        // Intentar devolver cualquier PickSpawner de seguridad guardado al hacer clic en el inventario
        if (event.getWhoClicked() instanceof Player player) {
            tryRestoreSavedItems(player);
        }

        // 0. Bloquear guardar o interactuar con un Saco (Bundle)
        if (isPickSpawner(cursor) && current != null && current.getType() == Material.BUNDLE || isPickSpawner(current) && cursor.getType() == Material.BUNDLE) {
            event.setCancelled(true);
            return;
        }

        // 1. Evitar Shift-Click desde el inventario del jugador hacia un inventario no permitido
        if (event.isShiftClick() && !topIsAllowed) {
            ItemStack clickedItem = event.getCurrentItem();
            if (isPickSpawner(clickedItem)) {
                event.setCancelled(true);
                return;
            }
        }

        // 2. Evitar colocar el PickSpawner (que está en el cursor) en un inventario no permitido
        if (isPickSpawner(event.getCursor()) && !isAllowedInventory(clickedInventory)) {
            event.setCancelled(true);
            return;
        }

        // 3. Evitar intercambiar el PickSpawner usando las teclas rápidas (números 1-9) en inventarios no permitidos
        if (event.getClick() == ClickType.NUMBER_KEY && !isAllowedInventory(clickedInventory)) {
            int hotbarSlot = event.getHotbarButton();
            Player player = (Player) event.getWhoClicked();
            ItemStack hotbarItem = player.getInventory().getItem(hotbarSlot);

            if (isPickSpawner(hotbarItem)) {
                event.setCancelled(true);
                return;
            }
        }

        // 4. Doble clic (recoger todos los del mismo tipo) si puede moverlo a un inventario no permitido
        if (event.getClick() == ClickType.DOUBLE_CLICK && !topIsAllowed) {
            if (isPickSpawner(event.getCursor())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Evita que el PickSpawner sea arrastrado (drag click) a inventarios no permitidos.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack draggedItem = event.getOldCursor();
        if (!isPickSpawner(draggedItem)) return;

        Inventory topInventory = event.getView().getTopInventory();
        if (isAllowedInventory(topInventory)) return;

        int topSize = topInventory.getSize();
        for (int rawSlot : event.getRawSlots()) {
            // Si el slot arrastrado pertenece al inventario superior no permitido
            if (rawSlot < topSize) {
                event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * Evita que el PickSpawner se dropee en el suelo al morir el jugador.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> drops = event.getDrops();
        List<ItemStack> saved = savedDeathItems.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());

        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (isPickSpawner(item)) {
                saved.add(item.clone());
                iterator.remove(); // Evita que caiga al suelo
            }
        }
    }

    /**
     * Devuelve el PickSpawner guardado al jugador cuando este reaparece.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        tryRestoreSavedItems(player);
    }

    /**
     * Recupera el PickSpawner del cursor si el jugador cierra su inventario
     * (por ejemplo, con el inventario lleno) y lo guarda de forma segura.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        ItemStack cursor = event.getView().getCursor();
        if (isPickSpawner(cursor)) {
            event.getView().setCursor(null);
            Player player = (Player) event.getPlayer();

            // Intentamos meterlo de nuevo al inventario
            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(cursor);
            if (!leftovers.isEmpty()) {
                // Si el inventario está lleno, lo mandamos a su EnderChest
                for (ItemStack leftover : leftovers.values()) {
                    Map<Integer, ItemStack> enderLeftovers = player.getEnderChest().addItem(leftover);
                    if (!enderLeftovers.isEmpty()) {
                        // Si el EnderChest también está lleno, lo guardamos en la cola de seguridad
                        for (ItemStack reallyLeftover : enderLeftovers.values()) {
                            savedDeathItems.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(reallyLeftover);
                            player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                                    .deserialize("<red>¡Inventario y EnderChest llenos! Tu PickSpawner se guardó de forma segura. Libera espacio para recuperarlo."));
                        }
                    }
                }
            }
        }
    }

    /**
     * Intenta devolver de forma segura los PickSpawners almacenados en la cola de seguridad.
     */
    private void tryRestoreSavedItems(Player player) {
        List<ItemStack> saved = savedDeathItems.get(player.getUniqueId());
        if (saved != null && !saved.isEmpty()) {
            Iterator<ItemStack> iterator = saved.iterator();
            while (iterator.hasNext()) {
                ItemStack item = iterator.next();
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
                if (leftovers.isEmpty()) {
                    iterator.remove();
                } else {
                    item.setAmount(leftovers.get(0).getAmount());
                    break; // Sigue lleno
                }
            }
            if (saved.isEmpty()) {
                savedDeathItems.remove(player.getUniqueId());
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                        .deserialize("<green>¡Tu PickSpawner guardado ha sido devuelto a tu inventario!"));
            }
        }
    }

    /**
     * Evita colocar o interactuar con el PickSpawner en bloques interactivos como estanterías cinceladas,
     * vasijas decoradas, atriles, cofres, etc.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            ItemStack item = event.getItem();
            if (isPickSpawner(item)) {
                String blockName = event.getClickedBlock().getType().name();

                // Evitamos el uso de isInteractable() obsoleto (deprecated) validando por grupos de nombres clave.
                // Esto bloquea Estanterías (SHELF/BOOKSHELF), Vasijas/Jarrones (POT), Atriles, Cofres, Barriles, Tolvas y Hornos.
                if (blockName.contains("SHELF") ||
                        blockName.contains("BOOKSHELF") ||
                        blockName.contains("POT") ||
                        blockName.contains("LECTERN") ||
                        blockName.contains("CHEST") ||
                        blockName.contains("BARREL") ||
                        blockName.contains("SHULKER") ||
                        blockName.contains("HOPPER") ||
                        blockName.contains("FURNACE") ||
                        blockName.contains("SMOKER")) {

                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Evita dar el PickSpawner a un Allay o colocarlo en un Item Frame / Glow Item Frame.
     // ... existing code ...

    /**
     * Evita dar el PickSpawner a un Allay o colocarlo en un Item Frame / Glow Item Frame.
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        EntityType type = event.getRightClicked().getType();

        // Bloquear dar al Allay, colocar en Marcos de Items (Item Frames) o Marcos Brillantes
        if (type == EntityType.ALLAY ||
                type == EntityType.ITEM_FRAME ||
                type == EntityType.GLOW_ITEM_FRAME) {

            Player player = event.getPlayer();
            ItemStack item = event.getHand() == org.bukkit.inventory.EquipmentSlot.HAND ?
                    player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();

            if (isPickSpawner(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void destroyBlock(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        boolean isPickSpawner = false;
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String dataContainerPlayer = meta.getPersistentDataContainer().get(
                        new NamespacedKey(plugin, "custom_item_id"),
                        PersistentDataType.STRING
                );
                if ("custom_item_pickspawner".equals(dataContainerPlayer)) {
                    isPickSpawner = true;
                }
            }
        }
        if(material != Material.SPAWNER && isPickSpawner){
            event.setCancelled(true);
        }
    }
}