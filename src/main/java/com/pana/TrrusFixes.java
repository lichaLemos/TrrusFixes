package com.pana;

import com.pana.advancedenchantments.ECommandExecutor;
import com.pana.advancedenchantments.ItemProtectionListener;
import com.pana.advancedenchantments.TemplateApplyListener;
import com.pana.advancedenchantments.gui.MenuListener;
import com.pana.advancedenchantments.recipes.RecipeRegistry;
import com.pana.auramobs.DeathMob;
import com.pana.cmi.enableCMIFix;
import com.pana.citizens.enableNPCSHOP;
import com.pana.crate.CrateListener;
import com.pana.mechanics.mobblock.destroyBlockMob;
import com.pana.mechanics.pickspawner.ItemProtection;
import com.pana.mechanics.pickspawner.Listeners;
import com.pana.mechanics.pickspawner.OfflinePDCQueue;
import com.pana.mechanics.strongermobs.StrongerMobsListener;
import dev.aurelium.auramobs.AuraMobs;
import org.bukkit.Bukkit;
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

        // REGISTRO DEL LISTENER DE CUSTOM DROP MOBS
        getServer().getPluginManager().registerEvents(
                new DeathMob(recipeRegistry.getItemRegistry()),
                this
        );

        // REGISTRO DEL DESTROY BLOCK MOB
        AuraMobs auraMobsPlugin = (AuraMobs) Bukkit.getPluginManager().getPlugin("AuraMobs");
        getServer().getPluginManager().registerEvents(
                new destroyBlockMob(this, auraMobsPlugin),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ItemProtection(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new Listeners(this) {
                },
                this
        );

        getServer().getPluginManager().registerEvents(
                new StrongerMobsListener(auraMobsPlugin),
                this
        );

        OfflinePDCQueue offlinePDCQueue = new OfflinePDCQueue(this);
        getServer().getPluginManager().registerEvents(offlinePDCQueue, this);

        // 3. Registro del Comando
        TrrusCommand trrusCommand = new TrrusCommand(this,
                recipeRegistry.getItemRegistry());

        var command = getCommand("trrus");
        if (command != null) {
            command.setExecutor(trrusCommand);
            command.setTabCompleter(trrusCommand);
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