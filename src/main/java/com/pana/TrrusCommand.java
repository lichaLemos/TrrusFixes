package com.pana;

import com.pana.advancedenchantments.GiveCommand;
import com.pana.advancedenchantments.items.CustomItemRegistry;
import com.pana.mechanics.pickspawner.commands.PickSpawner;
import com.pana.mechanics.pickspawner.commands.SpawnerCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TrrusCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final CustomItemRegistry itemRegistry;

    public TrrusCommand(JavaPlugin plugin, CustomItemRegistry itemRegistry) {
        this.plugin = plugin;
        this.itemRegistry = itemRegistry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("pickspawner")) {
            PickSpawner pickSpawner = new PickSpawner(plugin);
            return pickSpawner.onCommand(sender, command, label, args);
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("give")) {
            GiveCommand giveCommand = new GiveCommand(itemRegistry);
            return giveCommand.onCommand(sender, command, label, args);
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("spawner")) {
            SpawnerCommand spawnerCommand = new SpawnerCommand(plugin);
            return spawnerCommand.onCommand(sender, command, label, args);
        }

        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso correcto: /trrus <give|pickspawner|spawner> ..."));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("give", "pickspawner", "spawner"), completions);
            return completions;
        }

        if (args[0].equalsIgnoreCase("pickspawner")) {
            PickSpawner pickSpawner = new PickSpawner(plugin);
            return pickSpawner.onTabComplete(sender, command, label, args);
        }
        if (args[0].equalsIgnoreCase("give")) {
            GiveCommand giveCommand = new GiveCommand(itemRegistry);
            return giveCommand.onTabComplete(sender, command, label, args);
        }
        if (args[0].equalsIgnoreCase("spawner")) {
            SpawnerCommand spawnerCommand = new SpawnerCommand(plugin);
            return spawnerCommand.onTabComplete(sender, command, label, args);
        }

        return List.of();
    }
}