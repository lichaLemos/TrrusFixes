package com.pana;

import com.pana.advancedenchantments.GiveCommand;
import com.pana.advancedenchantments.ItemProtectionListener;
import com.pana.advancedenchantments.TemplateApplyListener;
import com.pana.advancedenchantments.recipes.RecipeRegistry;
import com.pana.bugs.cmi.enableCMIFix;
import com.pana.citizens.enableNPCSHOP;
import com.pana.crate.CrateListener;
import com.pana.debug.nitroCasino.enableFix;
import org.bukkit.plugin.java.JavaPlugin;

public class TrrusFixes extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        getLogger().info("¡TrrusFixes cargado correctamente!");

        // Carga o crea el config.yml por defecto
        saveDefaultConfig();

        // Carga los fixes
        enableCMIFix cmiFix = new enableCMIFix(this);
        cmiFix.enableFix();

        enableFix casinoFix = new enableFix(this);
        if(casinoFix.enableNitroCasinoFix()){
            getLogger().info("NitroCasinoFix fix applied.");
        }

        // Carga npc shop
        enableNPCSHOP NPCSHOP = new enableNPCSHOP(this);
        if(NPCSHOP.enableFix()){
            getLogger().info("Módulo NPC ShopGui+ inicializado correctamente.");
        }

        // 1. Inicializar el registro de recetas e ítems
        RecipeRegistry recipeRegistry = new RecipeRegistry(this);
        recipeRegistry.registerAll();

        // 2. Registrar el listener pasando la instancia de CustomItemRegistry
        getServer().getPluginManager().registerEvents(
                new TemplateApplyListener(recipeRegistry.getItemRegistry()),
                this
        );

        GiveCommand giveCommand = new GiveCommand(recipeRegistry.getItemRegistry());

        var command = getCommand("trrus");
        if (command != null) {
            command.setExecutor(giveCommand);
            command.setTabCompleter(giveCommand);
        } else {
            getLogger().severe("No se pudo registrar el comando '/trrus'. ¿Está declarado en el plugin.yml?");
        }

        // Registrar el listener de protección de ítems
        getServer().getPluginManager().registerEvents(
                new ItemProtectionListener(recipeRegistry.getItemRegistry()),
                this
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("¡TrrusFixes desactivado!");
    }
}