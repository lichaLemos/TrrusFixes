package com.pana.citizens;

import com.pana.TrrusFixes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class enableNPCSHOP {
    private final TrrusFixes plugin;

    public enableNPCSHOP(TrrusFixes plugin){
        this.plugin = plugin;
    }

    public boolean enableFix() {
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin("NitroCasino");

        if(targetPlugin == null){
            return false;
        }
        if(targetPlugin.isEnabled()) {
            plugin.getServer().getPluginManager().registerEvents(new NPCClickListener(), plugin);
            return true;
        }
        return false;
    }
}
