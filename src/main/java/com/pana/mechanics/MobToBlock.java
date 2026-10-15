package com.pana.mechanics;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public enum MobToBlock {
    SOUL_SOIL(Material.SOUL_SOIL, EntityType.WITHER_SKELETON, 8),
    SOUL_SAND(Material.SOUL_SAND, EntityType.BLAZE, 4);

    private final Material material;
    private final EntityType mob;
    private final double chance;

    MobToBlock(Material material, EntityType mob, double chance) {
        this.material = material;
        this.mob = mob;
        this.chance = chance;
    }

    public Material getMaterial() {
        return material;
    }

    public EntityType getMob() {
        return mob;
    }

    public double getChance() {
        return chance;
    }

    public static MobToBlock fromMaterial(Material material) {
        for (MobToBlock value : values()) {
            if (value.getMaterial() == material) {
                return value;
            }
        }

        return null;
    }
}