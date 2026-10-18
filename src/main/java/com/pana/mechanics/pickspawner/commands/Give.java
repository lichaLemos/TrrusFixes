package com.pana.mechanics.pickspawner.commands;

import com.pana.mechanics.pickspawner.ItemCreation;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Give {

    private final JavaPlugin plugin;

    public Give(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public Integer giveItem(Player player, int level, int uses) {

        if(player == null){
            return 0; //El jugador no existe
        }

        if(!player.isOnline()){
            return 1; //El jugador no está en línea
        }

        PickSpawner pickSpawner = new PickSpawner(plugin);

        if(player.getPersistentDataContainer().has(pickSpawner.pickSpawnerKey)){
            return 2; //El jugador ya tiene un PickSpawner registrado.
        }
        else{
            ItemCreation itemCreation = new ItemCreation(plugin);
            player.getPersistentDataContainer().set(pickSpawner.pickSpawnerKey, PersistentDataType.INTEGER, level);
            player.getInventory().addItem(itemCreation.buildPickaxe(level, uses));
            return -1;
        }
    }

}