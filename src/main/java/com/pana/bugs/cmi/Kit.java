package com.pana.bugs.cmi;

import com.Zrips.CMI.CMI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class Kit implements Listener {

    private final HashMap<String, Long> kitCooldowns = new HashMap<>();

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
            if (parts[0].equals("kit") && parts.length > 1) {
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

            String uniqueKey = player.getUniqueId().toString() + "_" + kitName;
            long currentTime = System.currentTimeMillis();

            if (kitCooldowns.containsKey(uniqueKey)) {
                long expirationTime = kitCooldowns.get(uniqueKey);

                if (currentTime < expirationTime) {
                    event.setCancelled(true);
                    String textoTraducido = ChatColor.translateAlternateColorCodes('&', "&#E20000&lᴇʀʀᴏʀ &8&l• &cᴀɢᴜᴀʀᴅᴇ ᴇʟ ᴛɪᴇᴍᴘᴏ ᴅᴇ ᴇɴғʀɪᴀᴍɪᴇɴᴛᴏ");
                    player.sendMessage(textoTraducido);
                    return;
                }
            }

            // Si pasa la validación, permitimos que CMI ejecute el comando original pero guardamos el cooldown
            long nextAvailable = currentTime + TimeUnit.SECONDS.toMillis(cooldownSeconds);
            kitCooldowns.put(uniqueKey, nextAvailable);
        }
    }

    private long getCooldownForKit(String kitName) {
        try {
            var kit = CMI.getInstance().getKitsManager().getKit(kitName.toLowerCase());
            if (kit != null) {
                return kit.getDelay();
            }
        } catch (Exception ignored) { }
        return 0;
    }
}