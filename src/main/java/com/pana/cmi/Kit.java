package com.pana.cmi;

import com.Zrips.CMI.CMI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class Kit implements Listener {

    private static final HashMap<String, Long> kitCooldowns = new HashMap<>();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase().trim();

        if (msg.startsWith("/")) {
            msg = msg.substring(1);
        }

        // Equivalente a type: player_command y %command% startsWith /cmi kit
        if (msg.startsWith("cmi kit") || msg.startsWith("kit")) {
            Player player = event.getPlayer();
            String[] parts = msg.split("\\s+");

            // %args_length% == 1 (Ej: /cmi kit o /kit) -> Redirige al menú "kits"
            if (parts.length == 1 || (parts.length == 2 && parts[0].equals("cmi"))) {
                event.setCancelled(true);
                player.performCommand("kits");
                return;
            }

            // %args_length% == 2 o más (Ej: /cmi kit playero o /kit playero)
            String kitName = "";
            if (parts[0].equals("kit")) {
                kitName = parts[1];
            } else if (parts.length > 2 && parts[0].equals("cmi") && parts[1].equals("kit")) {
                kitName = parts[2];
            }

            if (kitName.isEmpty()) return;

            if (player.hasPermission("cmi.kit.bypass.time")) {
                return;
            }

            long cooldownSeconds = getCooldownForKit(kitName);
            if (cooldownSeconds <= 0) return;

            String uniqueKey = player.getUniqueId().toString() + "_" + kitName.toLowerCase();
            long currentTime = System.currentTimeMillis();

            if (kitCooldowns.containsKey(uniqueKey)) {
                long expirationTime = kitCooldowns.get(uniqueKey);

                if (currentTime < expirationTime) {
                    event.setCancelled(true);
                    Component errorMessage = MINI_MESSAGE.deserialize("<#E20000><bold>ᴇʀʀᴏʀ</bold> <dark_gray><bold>•</bold></dark_gray> <red>ᴀɢᴜᴀʀᴅᴇ ᴇʟ ᴛɪᴇᴍᴘᴏ ᴅᴇ ᴇɴғʀɪᴀᴍɪᴇɴᴛᴏ");
                    player.sendMessage(errorMessage);
                }
            }
            // Ya no aplicamos el cooldown aquí. Dejamos que pase la validación y que CMI intente entregar el kit.
        }
    }

    public static void applyCooldown(Player player, String kitName, long cooldownSeconds) {
        if (player.hasPermission("cmi.kit.bypass.time")) {
            return;
        }
        if (cooldownSeconds <= 0) return;

        String uniqueKey = player.getUniqueId().toString() + "_" + kitName.toLowerCase();
        long currentTime = System.currentTimeMillis();

        // Aplicamos el cooldown ahora que el jugador ha reclamado/recibido el kit con éxito
        long nextAvailable = currentTime + TimeUnit.SECONDS.toMillis(cooldownSeconds);
        kitCooldowns.put(uniqueKey, nextAvailable);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("trrus.admin") && !sender.isOp()) {
            sender.sendMessage(MINI_MESSAGE.deserialize("<red>No tienes permisos para ejecutar este comando."));
        }

        if (args.length < 4 || !args[1].equalsIgnoreCase("cooldown")) {
            sender.sendMessage(MINI_MESSAGE.deserialize("<red>Uso correcto: /trrus kit cooldown <playerName> <kitName>"));
        }

        String playerName = args[2];
        String kitName = args[3];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(MINI_MESSAGE.deserialize("<red>El jugador no está en línea."));
        }

        long cooldownSeconds = getCooldownForKit(kitName);
        if (cooldownSeconds <= 0) {
            sender.sendMessage(MINI_MESSAGE.deserialize("<red>El kit '" + kitName + "' no existe o no tiene cooldown."));
        }

        assert player != null;
        applyCooldown(player, kitName, cooldownSeconds);
        sender.sendMessage(MINI_MESSAGE.deserialize("<green>Cooldown de " + cooldownSeconds + " segundos aplicado para el kit " + kitName + " al jugador " + player.getName() + "."));
        return true;
    }

    public @NonNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("trrus.admin") && !sender.isOp()) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], List.of("cooldown"), completions);
            return completions;
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("cooldown")) {
            List<String> playerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> playerNames.add(p.getName()));
            StringUtil.copyPartialMatches(args[2], playerNames, completions);
            return completions;
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("cooldown")) {
            List<String> kits = new ArrayList<>();
            try {
                var kitsMap = CMI.getInstance().getKitsManager().getKitMap();
                if (kitsMap != null) {
                    kits.addAll(kitsMap.keySet());
                }
            } catch (Exception ignored) {}
            StringUtil.copyPartialMatches(args[3], kits, completions);
            return completions;
        }

        return List.of();
    }

    private static long getCooldownForKit(String kitName) {
        try {
            var kit = CMI.getInstance().getKitsManager().getKit(kitName.toLowerCase());
            if (kit != null) {
                return kit.getDelay();
            }
        } catch (Exception ignored) { }
        return 0;
    }
}