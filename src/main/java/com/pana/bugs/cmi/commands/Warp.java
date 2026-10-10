package com.pana.bugs.cmi.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class Warp implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase().trim();

        if (msg.startsWith("/")) {
            msg = msg.substring(1);
        }

        // Equivalente a type: player_command y %command% startsWith /cmi warp
        if (msg.startsWith("cmi warp") || msg.startsWith("warp")) {
            Player player = event.getPlayer();
            String[] parts = msg.split("\\s+");

            // %args_length% == 1 (Ej: /cmi warp o /warp) -> Redirige al menú "warps"
            if (parts.length == 1 || (parts.length == 2 && parts[0].equals("cmi"))) {
                event.setCancelled(true);
                player.performCommand("warps");
                return;
            }

            // Si especifica un argumento (%args_length% == 2),
            // cancelamos el comando actual y ejecutamos limpiamente el comando interno de CMI con el argumento
            if (parts.length >= 2) {
                String warpName = parts[parts.length - 1]; // Toma el último argumento como el nombre del warp
                event.setCancelled(true);
                player.performCommand("cmi warp " + warpName);
            }
        }
    }
}