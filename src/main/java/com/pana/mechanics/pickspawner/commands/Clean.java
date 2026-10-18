package com.pana.mechanics.pickspawner.commands;

import com.pana.mechanics.pickspawner.OfflinePDCQueue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Clean {

    private final JavaPlugin plugin;

    public Clean(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public Integer remove(String player) {

        Player Player = Bukkit.getPlayer(player);

        if(Player == null){
            return 0; // El jugador no existe
        }

        OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(player);

        if (!offlinePlayer.isOnline()) {
            OfflinePDCQueue queue = new OfflinePDCQueue(plugin);
            queue.queueRemoval(offlinePlayer);

            // Notificas al administrador que el jugador está desconectado pero su limpieza se programó con éxito.
            return 3;
        }

        PickSpawner pickSpawner = new PickSpawner(plugin);

        if(Player.getPersistentDataContainer().has(pickSpawner.pickSpawnerKey)){

            Player.getPersistentDataContainer().remove(pickSpawner.pickSpawnerKey);

            return -1;
        }
        else{
            return 2; // El jugador no tiene un PickSpawner registrado
        }
    }

}
