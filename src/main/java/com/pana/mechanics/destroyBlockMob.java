package com.pana.mechanics;

import dev.aurelium.auraskills.api.event.skill.EntityXpGainEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class destroyBlockMob implements Listener {


    private final JavaPlugin plugin;

    public destroyBlockMob(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void destroyBlock(BlockBreakEvent event) {
        Material block = event.getBlock().getType();

        MobToBlock mobToBlock = MobToBlock.fromMaterial(block);

        if (mobToBlock == null) {
            return;
        }

        Location location = event.getBlock().getLocation().add(0.5, 1, 0.5);

        if (ThreadLocalRandom.current().nextDouble(100.0) < mobToBlock.getChance()) {
            Class<? extends Entity> entityClass = mobToBlock.getMob().getEntityClass();
            if (entityClass != null) {
                location.getWorld().spawn(location, entityClass, entity -> {
                    // 1. Guardamos tu tag propio para que no tire XP ni drops al morir
                    NamespacedKey key = new NamespacedKey(plugin, "destroy_block_mob");
                    entity.getPersistentDataContainer().set(key, PersistentDataType.STRING, "mob");

                    // 2. Engañamos a AuraMobs poniéndole su propia Summon Key para que lo ignore
                    Plugin auraMobsPlugin = Bukkit.getPluginManager().getPlugin("AuraMobs");
                    if (auraMobsPlugin instanceof dev.aurelium.auramobs.AuraMobs auraMobs) {
                        entity.getPersistentDataContainer().set(auraMobs.getSummonKey(), PersistentDataType.BYTE, (byte) 1);
                    }

                    // 3. Le aplicamos el nombre que nosotros queremos sin que nadie lo pise
                    String formattedName = formatMobName(mobToBlock.getMob().name());
                    entity.customName(LegacyComponentSerializer.legacySection().deserialize("§6Alma de " + formattedName));
                    entity.setCustomNameVisible(true);

                });
            }
        }
    }

    private String formatMobName(String rawName) {
        String[] parts = rawName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    @EventHandler
    public void noDrop(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        NamespacedKey key = new NamespacedKey(plugin, "destroy_block_mob");
        PersistentDataContainer data = entity.getPersistentDataContainer();
        if(data.has(key)){
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onAuraSkillsXpGain(EntityXpGainEvent event) {
        Entity entity = event.getAttacked(); // El mob que fue atacado o asesinado
        if (entity == null) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "destroy_block_mob");
        if (entity.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            // Cancelamos que el jugador reciba XP de AuraSkills por este mob
            event.setCancelled(true);
        }
    }


}
