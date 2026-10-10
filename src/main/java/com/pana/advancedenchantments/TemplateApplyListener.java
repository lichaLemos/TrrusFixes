package com.pana.advancedenchantments;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import net.advancedplugins.ae.api.AEAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TemplateApplyListener implements Listener {

    private final CustomItemRegistry itemRegistry;

    // Inyectamos la fábrica de ítems para acceder a la clave de PDC
    public TemplateApplyListener(CustomItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    private boolean isNotItemValid(ItemStack item) {
        return item == null || item.getType().isAir() || item.isEmpty();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;

        ItemStack cursorItem = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();

        if (isNotItemValid(cursorItem) || isNotItemValid(currentItem)) return;
        if (!isUpgradeTemplate(cursorItem)) return;
        if (!AEAPI.isCustomEnchantBook(currentItem)) return;

        Player player = (Player) event.getWhoClicked();

        String enchantName = AEAPI.getBookEnchantment(currentItem);
        int currentLevel = AEAPI.getBookEnchantmentLevel(currentItem);
        int maxLevel = AEAPI.getHighestEnchantmentLevel(enchantName);

        String enchantGroup = AEAPI.getEnchantGroupFromBook(currentItem);
        String templateTier = getTemplateTier(cursorItem); // Devuelve "template_common", "template_rare", etc.

        // Extraer la calidad ("common", "rare") quitando el prefijo "template_" si es necesario
        String cleanTier = templateTier.replace("template_", "");

        // Validar que la categoría coincida con la calidad del libro
        if (!enchantGroup.equalsIgnoreCase(cleanTier)) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Esta plantilla no coincide con la calidad del libro."));
            return;
        }

        if (currentLevel >= maxLevel) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Este libro ya se encuentra en su nivel máximo."));
            return;
        }

        event.setCancelled(true);

        int newLevel = currentLevel + 1;
        int successChance = AEAPI.getSuccessOnBook(currentItem);
        int destroyChance = AEAPI.getFailureOnBook(currentItem);

        ItemStack newBook = AEAPI.createEnchantmentBook(enchantName, newLevel, successChance, destroyChance, null);
        event.setCurrentItem(newBook);

        if (cursorItem.getAmount() > 1) {
            cursorItem.setAmount(cursorItem.getAmount() - 1);
        } else {
            event.getView().setCursor(null);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.2f);
        Component message = MiniMessage.miniMessage().deserialize(
                "<green>¡Has mejorado el libro a Nivel <level>!",
                Placeholder.parsed("level", String.valueOf(newLevel))
        );
        player.sendMessage(message);
    }

    private boolean isUpgradeTemplate(ItemStack item) {
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(itemRegistry.getItemKey(), PersistentDataType.STRING);
    }

    private String getTemplateTier(ItemStack item) {
        if (!item.hasItemMeta()) return "";
        return item.getItemMeta().getPersistentDataContainer().get(itemRegistry.getItemKey(), PersistentDataType.STRING);
    }
}