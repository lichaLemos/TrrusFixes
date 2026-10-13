package com.pana.advancedenchantments;

import com.pana.advancedenchantments.gui.UpgradeMenu;
import com.pana.advancedenchantments.items.CustomItemRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ECommandExecutor implements CommandExecutor, TabCompleter {

    private final CustomItemRegistry itemRegistry;

    public ECommandExecutor(CustomItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        // Si escriben /e upgrades
        if (args.length > 0 && (args[0].equalsIgnoreCase("upgrades") || args[0].equalsIgnoreCase("mejoras"))) {
            UpgradeMenu menu = new UpgradeMenu(itemRegistry);
            menu.openMainMenu(player);
            return true;
        }

        // Si escriben /e a secas u otros argumentos, ejecutamos el comando original de AdvancedEnchantments
        // (que por lo general suele registrarse como /ae o /enchantments, o podemos pasar el control).
        // Si AdvancedEnchantments usa /ae para su menú principal, puedes despacharlo así:
        player.performCommand("enchanter");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("upgrades","mejoras"), completions);
            return completions;
        }

        return List.of();
    }
}