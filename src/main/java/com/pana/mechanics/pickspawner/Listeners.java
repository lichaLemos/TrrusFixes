package com.pana.mechanics.pickspawner;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Listeners implements Listener {

    private final JavaPlugin plugin;

    public Listeners(JavaPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void destroyBlock(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        if(material == Material.SPAWNER){
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            // 1. Validar si la herramienta es el PickSpawner personalizado
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

            ItemCreation itemCreation = new ItemCreation(plugin);

            // 2. Cancelamos los drops vanilla por defecto para todos los casos
            event.setDropItems(false);

            event.setExpToDrop(0);

            BlockState state = event.getBlock().getState();

            if (state instanceof CreatureSpawner spawner) {
                EntityType spawnedType = spawner.getSpawnedType();

                if (spawnedType != null) {
                    if (isPickSpawner) {
                        // CASO 1: Es PickSpawner -> Entrega el item 'changeSpawner' (Spawner Cargado)
                        ItemStack customSpawner = itemCreation.changeSpawner(spawnedType);
                        event.getBlock().getWorld().dropItemNaturally(
                                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                                customSpawner
                        );
                    }
                    else if (item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                        // CASO 2: Tiene SilkTouch normal -> Entrega el item 'staySpawner' (Spawner Vacío)
                        ItemStack customSpawner = itemCreation.staySpawner(spawnedType);
                        event.getBlock().getWorld().dropItemNaturally(
                                event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                                customSpawner
                        );
                    }
                    // CASO 3: Cualquier otra herramienta/mano vacía -> No dropea nada (ya cubierto por setDropItems(false))
                }
            }
        }
    }

    /**
     * Controla la colocación de Spawners.
     * Solo permite colocar spawners que provengan de 'changeSpawner' (Cargados).
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.SPAWNER) {
            ItemStack item = event.getItemInHand();
            Player player = event.getPlayer();

            boolean isValidSpawner = false;
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    NamespacedKey customBlockKey = new NamespacedKey(plugin, "custom_block_id");
                    String value = meta.getPersistentDataContainer().get(customBlockKey, PersistentDataType.STRING);
                    if ("custom_block_spawner".equals(value)) {
                        isValidSpawner = true;
                    }
                }
            }

            // Si no es un changeSpawner (Spawner Cargado), cancelamos su colocación
            if (!isValidSpawner) {
                event.setCancelled(true);
                player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                        .deserialize("<red>No puedes colocar este spawner porque está vacío o no está cargado."));
                return;
            }

            // Guardamos el tag en la entidad del bloque en el mundo para que recuerde que es un spawner activo
            BlockState state = event.getBlockPlaced().getState();
            if (state instanceof CreatureSpawner spawner) {
                NamespacedKey customBlockKey = new NamespacedKey(plugin, "custom_block_id");
                spawner.getPersistentDataContainer().set(customBlockKey, PersistentDataType.STRING, "custom_block_spawner");

                // Extraemos explícitamente el tipo de mob del item para setearlo en el bloque colocado
                if (item.getItemMeta() instanceof org.bukkit.inventory.meta.BlockStateMeta blockStateMeta) {
                    if (blockStateMeta.getBlockState() instanceof CreatureSpawner itemSpawner) {
                        spawner.setSpawnedType(itemSpawner.getSpawnedType());
                    }
                }

                spawner.update(true, false);
            }
        }
    }

    /**
     * Controla la generación de mobs desde los Spawners.
     * Cancela la generación si el Spawner no posee el tag 'custom_block_spawner'.
     */
    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        CreatureSpawner spawner = event.getSpawner();
        NamespacedKey customBlockKey = new NamespacedKey(plugin, "custom_block_id");

        assert spawner != null;
        String customBlockId = spawner.getPersistentDataContainer().get(customBlockKey, PersistentDataType.STRING);

        // Si el bloque de spawner no tiene la firma correcta, cancelamos la generación del mob
        if (!"custom_block_spawner".equals(customBlockId)) {
            event.setCancelled(true);
        }
    }

    /**
     * Controla de forma dinámica el crafteo del spawner cargado (changeSpawner).
     * Hereda el tipo de mob del spawner original (S) colocado en el centro y bloquea
     * el uso de spawners vacíos (staySpawner) como ingrediente.
     */
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().getRecipe() instanceof ShapedRecipe shapedRecipe) {
            if (shapedRecipe.getKey().getKey().equals("craft_energized_spawner")) {
                ItemStack[] matrix = event.getInventory().getMatrix();

                // El ingrediente 'S' se encuentra en el centro de la matriz 3x3 (índice 4)
                ItemStack spawnerItem = matrix[4];

                if (spawnerItem == null || spawnerItem.getType() != Material.SPAWNER) {
                    event.getInventory().setResult(null);
                    return;
                }

                // Validamos que el spawner de entrada NO sea un spawner ya cargado (changeSpawner)
                boolean isAlreadyCharged = false;
                if (spawnerItem.hasItemMeta()) {
                    NamespacedKey customBlockKey = new NamespacedKey(plugin, "custom_block_id");
                    String value = spawnerItem.getItemMeta().getPersistentDataContainer().get(customBlockKey, PersistentDataType.STRING);
                    if ("custom_block_spawner".equals(value)) {
                        isAlreadyCharged = true;
                    }
                }

                if (isAlreadyCharged) {
                    event.getInventory().setResult(null);
                    return;
                }

                // Extraemos el tipo de entidad (mob) del spawner ingrediente
                EntityType spawnedType = EntityType.PIG; // Por defecto
                if (spawnerItem.getItemMeta() instanceof org.bukkit.inventory.meta.BlockStateMeta bsm) {
                    if (bsm.getBlockState() instanceof CreatureSpawner cs) {
                        spawnedType = cs.getSpawnedType();
                    }
                }

                // Modificamos el resultado del crafteo dinámicamente con el tipo de mob correcto
                ItemCreation itemCreation = new ItemCreation(plugin);
                event.getInventory().setResult(itemCreation.changeSpawner(spawnedType));
            }
            else if (shapedRecipe.getKey().getKey().equals("craft_netherite_pickspawner")) {
                // Modificamos el resultado del crafteo dinámicamente con el tipo de mob correcto
                ItemCreation itemCreation = new ItemCreation(plugin);
                event.getInventory().setResult(itemCreation.buildPickaxe(1, 20));
            }
        }
    }
}