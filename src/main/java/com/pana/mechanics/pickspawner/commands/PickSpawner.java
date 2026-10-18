package com.pana.mechanics.pickspawner.commands;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PickSpawner {

    private final JavaPlugin plugin;
    public final NamespacedKey pickSpawnerKey;

    public PickSpawner(JavaPlugin plugin) {
        this.plugin = plugin;
        pickSpawnerKey = new NamespacedKey(plugin, "pickspawner");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permisos para ejecutar este comando."));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso correcto: /trrus pickspawner <recharge|give|remove|clean>"));
            return true;
        }

        String subAction = args[1].toLowerCase();

        if (subAction.equals("recharge") || subAction.equals("remove") || subAction.equals("clean")) {
            if (args.length < 3) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso correcto: /trrus pickspawner " + subAction + " <nick>"));
                return true;
            }
            Player affected = Bukkit.getPlayer(args[2]);
            if (affected == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no existe o no está en línea."));
                return true;
            }

            if (subAction.equals("remove")) {
                Remove remove = new Remove(plugin);
                switch (remove.remove(affected)) {
                    case -1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Se removió el ítem exitosamente."));
                    case 0 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no existe."));
                    case 1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no está en línea."));
                    case 2 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no tiene un PickSpawner."));
                    case 3 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador tiene PickSpawner registrado, pero no lo tiene en su Inventario o EnderChest."));
                    default -> {}
                }
            } else if (subAction.equals("recharge")){
                Recharge recharge = new Recharge(plugin);
                switch (recharge.recharge(affected)) {
                    case -1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Se recargó el pico exitosamente."));
                    case 0 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no existe."));
                    case 1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no está en línea."));
                    case 2 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no tiene un PickSpawner."));
                    case 3 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador tiene PickSpawner registrado, pero no lo tiene en su Inventario o EnderChest."));
                    default -> {}
                }
            } else {
                Clean clean = new Clean(plugin);
                switch (clean.remove(args[2])) {
                    case -1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Se elimino el registro del jugador."));
                    case 0 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no existe."));
                    case 2 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no tiene un PickSpawner."));
                    case 3 ->player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>El jugador está desconectado. El registro se limpiará automáticamente cuando vuelva a conectarse."));
                    default -> {}
                }
            }
            return true;
        }

        if (subAction.equals("give")) {
            if (args.length < 4) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso correcto: /trrus pickspawner give <nick> <nivel> [usos]"));
                return true;
            }

            Player affected = Bukkit.getPlayer(args[2]);
            if (affected == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no existe o no está en línea."));
                return true;
            }

            String levelArg = args[3].toLowerCase();
            if (levelArg.equals("stone") || levelArg.equals("copper") || levelArg.equals("iron") || levelArg.equals("gold") || levelArg.equals("diamond")) {
                ItemStack customPick = getItemStack(levelArg);
                if (customPick != null) {
                    affected.getInventory().addItem(customPick);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>El pico custom de tipo <type> se dio exitosamente.").replaceText(b -> b.matchLiteral("<type>").replacement(levelArg)));
                }
                return true;
            }

            int level;
            try {
                level = Integer.parseInt(args[3]);
                if (level > 3 || level < 1) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El nivel debe estar entre 1 y 3 o ser uno de: stone, copper, iron, gold, diamond."));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El nivel debe ser un número válido (1-3) o uno de: stone, copper, iron, gold, diamond."));
                return true;
            }

            int uses = -2; // Por defecto o infinito
            if (args.length >= 5) {
                if (level == 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El PickSpawner de nivel 3 tiene usos infinitos, no se le puede definir una cantidad."));
                    return true;
                }
                try {
                    uses = Integer.parseInt(args[4]);
                    int maxUses = (level == 1) ? 20 : 80;
                    if (uses >= maxUses || uses < 0) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>La cantidad de usos para el nivel <level> debe ser entre 0 y <max>.",
                                Placeholder.parsed("max", String.valueOf(maxUses)),
                                Placeholder.parsed("level", String.valueOf(level))
                        ));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>La cantidad de usos debe ser un número válido."));
                    return true;
                }
            }

            Give give = new Give(plugin);
            switch (give.giveItem(affected, level, uses)) {
                case -1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<green>El pico se dio exitosamente."));
                case 0 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no existe."));
                case 1 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador no está en línea."));
                case 2 -> player.sendMessage(MiniMessage.miniMessage().deserialize("<red>El jugador ya tiene un PickSpawner registrado."));
                default -> {}
            }
            return true;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Subcomando desconocido. Usa recharge, give o remove."));
        return true;
    }

    private @org.jspecify.annotations.Nullable ItemStack getItemStack(String levelArg) {
        CustomItemRegistry itemRegistry = new CustomItemRegistry(plugin);
        return switch (levelArg) {
            case "stone" -> itemRegistry.createStonePickSpawner();
            case "copper" -> itemRegistry.createCopperPickSpawner();
            case "iron" -> itemRegistry.createIronPickSpawner();
            case "gold" -> itemRegistry.createGoldPickSpawner();
            case "diamond" -> itemRegistry.createDiamondPickSpawner();
            default -> null;
        };
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], List.of("give", "recharge", "remove", "clean"), completions);
            return completions;
        }

        if (args.length == 3) {
            List<String> playerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> playerNames.add(p.getName()));
            StringUtil.copyPartialMatches(args[2], playerNames, completions);
            return completions;
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("give")) {
            StringUtil.copyPartialMatches(args[3], List.of("1", "2", "3", "stone", "copper", "iron", "gold", "diamond"), completions);
            return completions;
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("give") && !args[3].equalsIgnoreCase("3")) {
            StringUtil.copyPartialMatches(args[4], List.of("10", "20", "50", "80"), completions);
            return completions;
        }

        return List.of();
    }
}