package com.pana;

import com.pana.advancedenchantments.ECommandExecutor;
import com.pana.advancedenchantments.GiveCommand;
import com.pana.advancedenchantments.ItemProtectionListener;
import com.pana.advancedenchantments.TemplateApplyListener;
import com.pana.advancedenchantments.gui.MenuListener;
import com.pana.advancedenchantments.recipes.RecipeRegistry;
import com.pana.bugs.cmi.enableCMIFix;
import com.pana.citizens.enableNPCSHOP;
import com.pana.crate.CrateListener;
import org.bukkit.plugin.java.JavaPlugin;

public class TrrusFixes extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CrateListener(this), this);
        getLogger().info("¡TrrusFixes cargado correctamente!");

        saveDefaultConfig();

        enableCMIFix cmiFix = new enableCMIFix(this);
        cmiFix.enableFix();

        enableNPCSHOP NPCSHOP = new enableNPCSHOP(this);
        if (NPCSHOP.enableFix()) {
            getLogger().info("Módulo NPC ShopGui+ inicializado correctamente.");
        }

        // 1. Registro de recetas e ítems
        RecipeRegistry recipeRegistry = new RecipeRegistry(this);
        recipeRegistry.registerAll();

        // 2. Registros de Listeners
        getServer().getPluginManager().registerEvents(
                new TemplateApplyListener(recipeRegistry.getItemRegistry()),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ItemProtectionListener(recipeRegistry.getItemRegistry()),
                this
        );

        // REGISTRO DEL LISTENER DEL MENÚ GUI
        getServer().getPluginManager().registerEvents(
                new MenuListener(recipeRegistry.getItemRegistry(), this),
                this
        );

        // 3. Registro del Comando
        GiveCommand giveCommand = new GiveCommand(recipeRegistry.getItemRegistry());

        var command = getCommand("trrus");
        if (command != null) {
            command.setExecutor(giveCommand);
            command.setTabCompleter(giveCommand);
        } else {
            getLogger().severe("No se pudo registrar el comando '/trrus'. ¿Está declarado en el plugin.yml?");
        }

        // REGISTRO DEL COMANDO /e PERSONALIZADO
        ECommandExecutor eCommand = new ECommandExecutor(recipeRegistry.getItemRegistry());
        var eCmdRef = getCommand("e");
        if (eCmdRef != null) {
            eCmdRef.setExecutor(eCommand);
        } else {
            getLogger().severe("No se pudo registrar el comando 'e'. ¿Está declarado en el plugin.yml?");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("¡TrrusFixes desactivado!");
    }
}