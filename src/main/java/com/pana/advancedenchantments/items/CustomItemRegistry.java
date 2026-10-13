package com.pana.advancedenchantments.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CustomItemRegistry {

    private final NamespacedKey itemKey;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public CustomItemRegistry(JavaPlugin plugin) {
        this.itemKey = new NamespacedKey(plugin, "custom_item_id");
    }

    public NamespacedKey getItemKey() {
        return itemKey;
    }

    // --- MÉTODOS PARA DIVERSOS ÍTEMS ---

    public ItemStack createFieryIron() {
        return buildItem(CustomItemType.FIERY_IRON, List.of(
                "<!italic><gray>Un lingote de hierro imbuido con polvo de Blaze.</gray>"
        ));
    }

    public ItemStack createGoldFieryCompact() {
        return buildItem(CustomItemType.GOLD_FIERY_COMPACT, List.of(
                "<!italic><gray>Oro comprimido con calor extremo.</gray>"
        ));
    }

    public ItemStack createGoldAiryCompact() {
        return buildItem(CustomItemType.GOLD_AIRY_COMPACT, List.of(
                "<!italic><gray>Oro infundido con esencia de aire.</gray>"
        ));
    }

    public ItemStack createGeodeFieryCompact() {
        return buildItem(CustomItemType.GEODE_FIERY_COMPACT, List.of(
                "<!italic><gray>Geoda comprimida con calor extremo.</gray>"
        ));
    }

    public ItemStack createGeodeAiryCompact() {
        return buildItem(CustomItemType.GEODE_AIRY_COMPACT, List.of(
                "<!italic><gray>Geoda infundida con esencia de aire.</gray>"
        ));
    }

    public ItemStack createGeodeCarrotCompact() {
        return buildItem(CustomItemType.GEODE_CARROT_COMPACT, List.of(
                "<!italic><gray>Geoda comprimida con zanahor dorado.</gray>"
        ));
    }

    public ItemStack createRedluziCompact() {
        return buildItem(CustomItemType.REDLUZI_COMPACT, List.of(
                "<!italic><gray>Aleación purpura.</gray>"
        ));
    }

    public ItemStack createRedluziFieryCompact() {
        return buildItem(CustomItemType.REDLUZI_FIERY_COMPACT, List.of(
                "<!italic><gray>Aleación purpura comprimida con calor extremo.</gray>"
        ));
    }

    public ItemStack createRedluziAiryCompact() {
        return buildItem(CustomItemType.REDLUZI_AIRY_COMPACT, List.of(
                "<!italic><gray>Aleación purpura infundida con esencia de aire.</gray>"
        ));
    }

    public ItemStack createRedluziCarrotCompact() {
        return buildItem(CustomItemType.REDLUZI_CARROT_COMPACT, List.of(
                "<!italic><gray>Aleación purpura infundida con esencia de aire.</gray>"
        ));
    }

    public ItemStack createRedluziAquaCompact() {
        return buildItem(CustomItemType.REDLUZI_AQUA_COMPACT, List.of(
                "<!italic><gray>Aleación purpura bañada con extracto de agua.</gray>"
        ));
    }

    public ItemStack createEmeraldAiryCompact() {
        return buildItem(CustomItemType.EMERALD_AIRY_COMPACT, List.of(
                "<!italic><gray>Esmeralda infundida con esencia de aire.</gray>"
        ));
    }

    public ItemStack createEmeraldCarrotCompact() {
        return buildItem(CustomItemType.EMERALD_CARROT_COMPACT, List.of(
                "<!italic><gray>Esmeralda infundida con esencia de aire.</gray>"
        ));
    }

    public ItemStack createEmeraldAquaCompact() {
        return buildItem(CustomItemType.EMERALD_AQUA_COMPACT, List.of(
                "<!italic><gray>Esmeralda bañada con extracto de agua.</gray>"
        ));
    }

    public ItemStack createEmeraldDeathCompact() {
        return buildItem(CustomItemType.EMERALD_DEATH_COMPACT, List.of(
                "<!italic><gray>Esmeralda mortal bañada en almas.</gray>"
        ));
    }

    public ItemStack createDiamondCarrotCompact() {
        return buildItem(CustomItemType.DIAMOND_CARROT_COMPACT, List.of(
                "<!italic><gray>Diamante infundido con esencia de aire.</gray>"
        ));
    }

    public ItemStack createDiamondAquaCompact() {
        return buildItem(CustomItemType.DIAMOND_AQUA_COMPACT, List.of(
                "<!italic><gray>Diamante bañado con extracto de agua.</gray>"
        ));
    }

    public ItemStack createDiamondDeathCompact() {
        return buildItem(CustomItemType.DIAMOND_DEATH_COMPACT, List.of(
                "<!italic><gray>Diamante mortal bañado en almas.</gray>"
        ));
    }

    public ItemStack createDiamondHornCompact() {
        return buildItem(CustomItemType.DIAMOND_HORN_COMPACT, List.of(
                "<!italic><gray>Cuerno con incrustaciones de Diamante.</gray>"
        ));
    }

    // --- MÉTODO GENERAL PARA PLANTILLAS ---

    public ItemStack createTemplate(CustomItemType templateType) {
        // Usa el Material definido en el Enum (ej.: VEX para Común, TIDE para Rara)
        ItemStack item = new ItemStack(templateType.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String color = templateType.getColorHex();
            String name = templateType.getDisplayName().toUpperCase();

            meta.displayName(mm.deserialize("<!italic><" + color + ">ᴍᴇᴊᴏʀᴀ ᴅᴇ ʟɪʙʀᴏ - " + name + "</" + color + ">"));

            //Template, hide Applies to: & Ingredients:

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize(" "));
            lore.add(mm.deserialize("<!italic><gray>Calidad:</gray> <" + color + ">" + name + "</" + color + ">"));
            lore.add(mm.deserialize("<!italic><dark_gray>Arrastra sobre un libro para subirlo de nivel.</dark_gray>"));
            lore.add(mm.deserialize(" "));
            meta.lore(lore);

            // Guardar identificador en PersistentDataContainer
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, templateType.getId());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildItem(CustomItemType type, List<String> loreLines) {
        ItemStack item = new ItemStack(type.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize("<!italic><" + type.getColorHex() + "><bold>" + type.getDisplayName() + "</bold></" + type.getColorHex() + ">"));
            List<Component> lore = new ArrayList<>();
            for (String line : loreLines) {
                lore.add(mm.deserialize(line));
            }

            //Template, hide Applies to: & Ingredients:

            meta.lore(lore);
            meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, type.getId());
            item.setItemMeta(meta);
        }
        return item;
    }
}