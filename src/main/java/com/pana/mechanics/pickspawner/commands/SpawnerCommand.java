package com.pana.mechanics.pickspawner.commands;

import com.pana.mechanics.pickspawner.ItemCreation;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpawnerCommand {

    private final JavaPlugin plugin;

    public SpawnerCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permisos para ejecutar este comando."));
            return true;
        }

        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso correcto: /trrus spawner give (changeSpawner|staySpawner) (mobtype) [amount]"));
            return true;
        }

        String spawnerType = args[2];
        if (!spawnerType.equalsIgnoreCase("changeSpawner") && !spawnerType.equalsIgnoreCase("staySpawner")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Tipo de spawner inválido. Debe ser changeSpawner o staySpawner."));
            return true;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Tipo de mob inválido."));
            return true;
        }

        int amount = 1;
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
                if (amount <= 0) amount = 1;
            } catch (NumberFormatException e) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>La cantidad debe ser un número válido."));
                return true;
            }
        }

        ItemCreation itemCreation = new ItemCreation(plugin);
        ItemStack spawnerItem;
        if (spawnerType.equalsIgnoreCase("changeSpawner")) {
            spawnerItem = itemCreation.changeSpawner(entityType);
        } else {
            spawnerItem = itemCreation.staySpawner(entityType);
        }

        spawnerItem.setAmount(amount);
        player.getInventory().addItem(spawnerItem);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>¡Has recibido x<amount> de Spawner de <mob>!",
                Placeholder.parsed("amount", String.valueOf(amount)),
                Placeholder.parsed("mob", entityType.name())
        ));
        return true;
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], List.of("give"), completions);
            return completions;
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("give")) {
            StringUtil.copyPartialMatches(args[2], List.of("changeSpawner", "staySpawner"), completions);
            return completions;
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("give")) {
            List<String> mobTypes = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (type.isSpawnable()) {
                    mobTypes.add(type.name().toLowerCase());
                }
            }
            StringUtil.copyPartialMatches(args[3].toLowerCase(), mobTypes, completions);
            return completions;
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("give")) {
            StringUtil.copyPartialMatches(args[4], List.of("1", "16", "32", "64"), completions);
            return completions;
        }

        return List.of();
    }
}