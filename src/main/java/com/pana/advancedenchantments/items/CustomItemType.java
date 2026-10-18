package com.pana.advancedenchantments.items;

import org.bukkit.Material;

public enum CustomItemType {
    // --- PICOS PICKSPAWNERS ---
    STONE_PICKSPAWNER("stone_pickspawner", Material.STONE_PICKAXE, "PickSpawner de Piedra", "#7E7E7E"),
    COPPER_PICKSPAWNER("copper_pickspawner", Material.COPPER_PICKAXE, "PickSpawner de Cobre", "#D36C47"),
    IRON_PICKSPAWNER("iron_pickspawner", Material.IRON_PICKAXE, "PickSpawner de Hierro", "#5B5B5B"),
    GOLD_PICKSPAWNER("gold_pickspawner", Material.GOLDEN_PICKAXE, "PickSpawner de Oro", "#E6AF15"),
    DIAMOND_PICKSPAWNER("diamond_pickspawner", Material.DIAMOND_PICKAXE, "PickSpawner de Diamante", "#2AC5AA"),
    // --- MATERIALES PRIMA ---
    BLAZE_ESSENCE("blaze_essence", Material.BLAZE_POWDER, "Esencia de Blaze", "#FFAA00"),
    BREEZE_ESSENCE("breeze_essence", Material.WIND_CHARGE, "Esencia de Breeze", "#6B75A9"),
    PIGLIN_ESSENCE("piglin_essence", Material.GOLDEN_CARROT, "Esencia de Piglin Brute", "#E9C944"),
    GUARDIAN_ESSENCE("guardian_essence", Material.PRISMARINE_SHARD, "Esencia de Guardian", "#A2CFC0"),
    WITHER_ESSENCE("wither_essence", Material.WITHER_ROSE, "Esencia de Wither", "#171210"),
    WARDEN_ESSENCE("warden_essence", Material.ECHO_SHARD, "Esencia de Warden", "#009093"),
    // --- MATERIALES INTERMEDIOS ---
    // Común
    FIERY_IRON("fiery_iron", Material.IRON_INGOT, "Hierro Ardiente", "#FFAA00"),
    // Raro
    GOLD_FIERY_COMPACT("gold_fiery_compact", Material.GOLD_INGOT, "Compacto Ardiente de Oro", "#F7D349"),
    GOLD_AIRY_COMPACT("gold_airy_compact", Material.GOLD_INGOT, "Compacto Aireado de Oro", "#F7D349"),
    // Épico
    GEODE_CARROT_COMPACT("geode_carrot_compact", Material.AMETHYST_SHARD, "Compacto Zanahor de Amatista", "#795AB3"),
    GEODE_FIERY_COMPACT("geode_fiery_compact", Material.AMETHYST_SHARD, "Compacto Ardiente de Amatista", "#795AB3"),
    GEODE_AIRY_COMPACT("geode_airy_compact", Material.AMETHYST_SHARD, "Compacto Aireado de Amatista", "#795AB3"),
    // Mítico
    REDLUZI_COMPACT("redluzi_compact", Material.PURPLE_GLAZED_TERRACOTTA, "Redluzi Compacto", "#591D8B"),
    REDLUZI_FIERY_COMPACT("redluzi_fiery_compact", Material.POPPED_CHORUS_FRUIT, "Compacto Ardiente de Redluzi", "#795AB3"),
    REDLUZI_AIRY_COMPACT("redluzi_airy_compact", Material.POPPED_CHORUS_FRUIT, "Compacto Aireado de Redluzi", "#795AB3"),
    REDLUZI_CARROT_COMPACT("redluzi_carrot_compact", Material.POPPED_CHORUS_FRUIT, "Compacto Zanahor de Redluzi", "#795AB3"),
    REDLUZI_AQUA_COMPACT("redluzi_aqua_compact", Material.POPPED_CHORUS_FRUIT, "Compacto Aquoso de Redluzi", "#795AB3"),
    // Legendario
    EMERALD_AIRY_COMPACT("emerald_airy_compact", Material.EMERALD, "Compacto Aireado de Esmeralda", "#55FF55"),
    EMERALD_CARROT_COMPACT("emerald_carrot_compact", Material.EMERALD, "Compacto Zanahor de Esmeralda", "#55FF55"),
    EMERALD_AQUA_COMPACT("emerald_aqua_compact", Material.EMERALD, "Compacto Aquoso de Esmeralda", "#55FF55"),
    EMERALD_DEATH_COMPACT("emerald_death_compact", Material.EMERALD, "Compacto Mortifero de Esmeralda", "#55FF55"),
    // Absoluto
    DIAMOND_CARROT_COMPACT("diamond_carrot_compact", Material.DIAMOND, "Compacto Zanahor de Diamante", "#3CDDE2"),
    DIAMOND_AQUA_COMPACT("diamond_aqua_compact", Material.DIAMOND, "Compacto Aquoso de Diamante", "#3CDDE2"),
    DIAMOND_DEATH_COMPACT("diamond_death_compact", Material.DIAMOND, "Compacto Mortifero de Diamante", "#3CDDE2"),
    DIAMOND_HORN_COMPACT("diamond_horn_compact", Material.DIAMOND, "Cuerno Endiamantado", "#3CDDE2"),

    // --- PLANTILLAS (TIERS) ---
    TEMPLATE_COMMON("template_common", Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, "Común", "#A9A9A9"),
    TEMPLATE_RARE("template_rare", Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, "Raro", "#55FF55"),
    TEMPLATE_EPIC("template_epic", Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, "Épico", "#AA00AA"),
    TEMPLATE_MYTHIC("template_mythic", Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, "Mítico", "#FF5555"),
    TEMPLATE_LEGENDARY("template_legendary", Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, "Legendario", "#FFAA00"),
    TEMPLATE_ABSOLUTE("template_absolute", Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, "Absoluto", "#00FFFF");

    private final String id;
    private final Material material;
    private final String displayName;
    private final String colorHex;

    CustomItemType(String id, Material material, String displayName, String colorHex) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.colorHex = colorHex;
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public String getColorHex() { return colorHex; }
}