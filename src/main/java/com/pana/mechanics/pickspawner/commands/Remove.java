package com.pana.mechanics.pickspawner.commands;

import com.pana.mechanics.pickspawner.Tracking;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Remove {

    private final JavaPlugin plugin;

    public Remove(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public Integer remove(Player player) {

        if(player == null){
            return 0; // El jugador no existe
        }

        if(!player.isOnline()){
            return 1; // El jugador no está en línea
        }

        PickSpawner pickSpawner = new PickSpawner(plugin);

        if(player.getPersistentDataContainer().has(pickSpawner.pickSpawnerKey)){

            Tracking tracking = new Tracking(plugin);
            int result = tracking.removeItem(player);

            // Si se removió el ítem exitosamente del inventario/enderchest (-1),
            // también eliminamos el registro del PersistentDataContainer del jugador.
            if(result == -1){
                player.getPersistentDataContainer().remove(pickSpawner.pickSpawnerKey);
            }

            return result;
        }
        else{
            return 2; // El jugador no tiene un PickSpawner registrado
        }
    }
}