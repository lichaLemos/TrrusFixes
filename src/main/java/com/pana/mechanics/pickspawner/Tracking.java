package com.pana.mechanics.pickspawner;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Tracking {

    private final NamespacedKey customItemKey;
    private ItemStack item;
    private Inventory inv;

    public Tracking(JavaPlugin plugin){
        ItemCreation itemCreation = new ItemCreation(plugin);
        this.customItemKey = itemCreation.customItemKey;
        this.item = new ItemStack(Material.AIR);
    }

    public Integer removeItem(Player player){
        findItem(player);
        if(this.item.getType() == Material.AIR){
            return 3; // No se encontró el ítem ni en inventario ni en enderchest
        }

        // Removemos exactamente 1 unidad del ítem encontrado
        ItemStack removedItem = this.item.clone();
        removedItem.setAmount(1);
        this.inv.removeItem(removedItem);

        return -1; // Se quitó el ítem con éxito
    }

    public Integer rechargeItem(Player player){
        findItem(player);
        if(this.item.getType() == Material.AIR){
            return 3; // No se encontró el ítem para reparar
        }

        ItemMeta meta = this.item.getItemMeta();
        if(meta instanceof Damageable damageable){
            damageable.setDamage(0);
            this.item.setItemMeta(meta);
        }

        return -1; // Se reparó el ítem con éxito
    }

    private void findItem(Player player){
        this.item = new ItemStack(Material.AIR);
        this.inv = null;

        // 1. Buscar primero en el inventario principal
        Inventory inventory = player.getInventory();
        for (ItemStack contentItem : inventory.getContents()) {
            if (contentItem != null && contentItem.getType() == Material.NETHERITE_PICKAXE) {
                ItemMeta meta = contentItem.getItemMeta();
                if (meta != null) {
                    String dataContainer = meta.getPersistentDataContainer().get(customItemKey, PersistentDataType.STRING);
                    if ("custom_item_pickspawner".equals(dataContainer)) {
                        this.item = contentItem;
                        this.inv = inventory;
                        return; // Encontrado en el inventario
                    }
                }
            }
        }

        // 2. Si no está en el inventario, buscar en el EnderChest
        Inventory enderChest = player.getEnderChest();
        for (ItemStack contentItem : enderChest.getContents()) {
            if (contentItem != null && contentItem.getType() == Material.NETHERITE_PICKAXE) {
                ItemMeta meta = contentItem.getItemMeta();
                if (meta != null) {
                    String dataContainer = meta.getPersistentDataContainer().get(customItemKey, PersistentDataType.STRING);
                    if ("custom_item_pickspawner".equals(dataContainer)) {
                        this.item = contentItem;
                        this.inv = enderChest;
                        return; // Encontrado en el EnderChest
                    }
                }
            }
        }
    }
}