package com.pana.bugs.cmi;

import com.pana.TrrusFixes;
import com.pana.bugs.cmi.commands.Warp;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class enableCMIFix {
    private final TrrusFixes plugin;

    public enableCMIFix(TrrusFixes plugin){
        this.plugin = plugin;
    }

    public void enableFix() {
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin("CMI");

        if(targetPlugin == null){
            return;
        }
        if(targetPlugin.isEnabled()) {
            if (warpFix()) {
                plugin.getLogger().info("Warp fix applied.");
            } else {
                plugin.getLogger().info("Warp fix don't applied.");
            }

            if(kitFix()){
                plugin.getLogger().info("Kit fix applied.");
            }else{
                plugin.getLogger().info("Kit fix don't applied.");
            }
        }
    }

    private boolean kitFix(){
        boolean isKitsFixEnabled = plugin.getConfig().getBoolean("Fix.Kits.enabled", false);
        if (isKitsFixEnabled) {
            // Se registra únicamente si está en true
            plugin.getServer().getPluginManager().registerEvents(new Kit(), plugin);
            return true;
        }
        return false;
    }

    private boolean warpFix(){
        boolean isWarpsFixEnabled = plugin.getConfig().getBoolean("Fix.Warps.enabled", false);
        if (isWarpsFixEnabled) {
            // Se registra únicamente si está en true
            plugin.getServer().getPluginManager().registerEvents(new Warp(), plugin);
            return true;
        }
        return false;
    }
}
