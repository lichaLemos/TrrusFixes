package com.pana.mechanics.pickspawner;

import com.pana.mechanics.pickspawner.commands.PickSpawner;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class OfflinePDCQueue implements Listener {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    public OfflinePDCQueue(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "offline_queue.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Registra que un jugador offline necesita ser limpiado del PersistentDataContainer.
     */
    public void queueRemoval(OfflinePlayer player) {
        List<String> queuedPlayers = config.getStringList("pending-removals");
        String uuidStr = player.getUniqueId().toString();
        if (!queuedPlayers.contains(uuidStr)) {
            queuedPlayers.add(uuidStr);
            config.set("pending-removals", queuedPlayers);
            save();
        }
    }

    /**
     * Verifica si el jugador tiene una limpieza pendiente al conectarse.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> queuedPlayers = config.getStringList("pending-removals");
        String uuidStr = player.getUniqueId().toString();

        if (queuedPlayers.contains(uuidStr)) {
            PickSpawner pickSpawner = new PickSpawner(plugin);

            // Eliminar el registro del PersistentDataContainer de manera segura ahora que está online
            if (player.getPersistentDataContainer().has(pickSpawner.pickSpawnerKey)) {
                player.getPersistentDataContainer().remove(pickSpawner.pickSpawnerKey);
            }

            // Remover de la lista de pendientes y guardar
            queuedPlayers.remove(uuidStr);
            config.set("pending-removals", queuedPlayers);
            save();

            plugin.getLogger().info("Se procesó la limpieza pendiente de PickSpawner para " + player.getName() + ".");
        }
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar el archivo offline_queue.yml: " + e.getMessage());
        }
    }
}