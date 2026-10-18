package com.pana.mechanics.pickspawner.commands;

import com.pana.mechanics.pickspawner.Tracking;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Recharge {

    private final JavaPlugin plugin;

    public Recharge(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public Integer recharge(Player player) {

        if(player == null){
            return 0; //El jugador no existe
        }

        if(!player.isOnline()){
            return 1; //El jugador no está en línea
        }

        PickSpawner pickSpawner = new PickSpawner(plugin);

        if(player.getPersistentDataContainer().has(pickSpawner.pickSpawnerKey)){

            Tracking tracking = new Tracking(plugin);

            return tracking.rechargeItem(player);
        }
        else{
            return 2; //El jugador no tiene un PickSpawner
        }
    }

}
