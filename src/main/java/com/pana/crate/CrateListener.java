package com.pana.crate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.CratesAPI;
import su.nightexpress.excellentcrates.api.crate.Reward;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;
import su.nightexpress.excellentcrates.crate.impl.Crate;

public class CrateListener implements Listener {

    private final Plugin plugin;

    public CrateListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrateOpen(CrateOpenEvent event) {
        if (event == null) return;

        Player player = event.getPlayer();
        Crate crate = event.getCrate();
        String crateName = crate.getId();
        List<String> cratesName = Arrays.asList(
                "legendary-star", "mythic-star", "epic-star", "rare-star", "common-star", "mysterious-star"
        );
        if(cratesName.contains(crateName)){
            event.setCancelled(true);
            ItemStack crateItem = crate.getItem();

            //Quita la llave
            ItemStack toRemove = crateItem.clone();
            toRemove.setAmount(1);
            player.getInventory().removeItem(toRemove);

            //Inicia la animación
            ItemStack totemItem = createAnimationItem();
            playTotemAnimation(player, totemItem);

            //Da la recompensa
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Reward reward = crate.rollReward(player);
                CratesAPI.getCrateManager().giveReward(player, reward);
            }, 40L);
        }
    }

    private void playTotemAnimation(@NotNull Player target, @NotNull ItemStack animationItem) {
        if (this.tryNativeTotemAnimation(target, animationItem)) {
            this.suppressTotemSound(target);
        } else {
            ItemStack realMain = target.getInventory().getItemInMainHand().clone();
            ItemStack realOff = target.getInventory().getItemInOffHand().clone();

            target.sendEquipmentChange(target, EquipmentSlot.HAND, animationItem);
            target.sendEquipmentChange(target, EquipmentSlot.OFF_HAND, animationItem);

            // Envío del efecto de tótem usando la API moderna para evitar el warning de deprecación
            try {
                target.sendEntityEffect(EntityEffect.valueOf("TOTEM_RESURRECT"), target);
            } catch (IllegalArgumentException e) {
                // Fallback si la versión cambia el nombre de la constante
                target.playEffect(target.getLocation(), org.bukkit.Effect.valueOf("TOTEM_RESURRECT"), null);
            }

            this.suppressTotemSound(target);

            // Usamos 'plugin' en lugar de 'this' para registrar la tarea en el Scheduler
            Bukkit.getScheduler().runTask(plugin, () -> {
                target.sendEquipmentChange(target, EquipmentSlot.HAND, realMain);
                target.sendEquipmentChange(target, EquipmentSlot.OFF_HAND, realOff);
                this.suppressTotemSound(target);
            });
        }
    }

    private void suppressTotemSound(@NotNull Player target) {
        target.stopSound(Sound.ITEM_TOTEM_USE);
        target.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS);
        //target.stopSound("Minecraft:item.totem.use");
        //target.stopSound("Minecraft:item.totem.use", SoundCategory.PLAYERS);
    }

    private boolean tryNativeTotemAnimation(@NotNull Player target, @NotNull ItemStack animationItem) {
        try {
            Method method = target.getClass().getMethod("sendTotemAnimation", ItemStack.class);
            method.invoke(target, animationItem);
            return true;
        } catch (ReflectiveOperationException var4) {
            return false;
        }
    }

    private @NotNull ItemStack createAnimationItem() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Asigna el modelo vanilla nativo de la Estrella del Nether en 1.21.4+
            meta.setItemModel(NamespacedKey.minecraft("nether_star"));
            item.setItemMeta(meta);
        }

        return item;
    }
}