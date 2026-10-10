package com.pana.debug.nitroCasino;

import com.pana.TrrusFixes;
import dev.redbill.nitrocasino.NitroCasino;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import static org.bukkit.Bukkit.getServer;

public class enableFix {
    private final TrrusFixes plugin;

    public enableFix(TrrusFixes plugin){
        this.plugin = plugin;
    }

    public boolean enableNitroCasinoFix() {
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin("NitroCasino");

        if(targetPlugin == null){
            return false;
        }
        if(targetPlugin.isEnabled()) {
            String versionText = targetPlugin.getPluginMeta().getVersion(); // Ejemplo: "1.3.0"
            // 1. Reemplazamos los puntos por texto vacío. "1.3.0" se convierte en "130"
            // (Usamos expresiones regulares: "\\." Representa un punto literal)
            String cleanVersion = versionText.replaceAll("\\.", "");

            // Limpiar sufijos comunes (Ej.: "1.3.0-SNAPSHOT" -> "130")
            if(cleanVersion.contains("-")) {
                cleanVersion = cleanVersion.split("-")[0];
            }

            int version = 0;

            try {
                // 2. Convertimos el String "130" a un int (130)
                version = Integer.parseInt(cleanVersion);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("No se pudo convertir la versión a número: " + versionText);
                plugin.getLogger().warning("Ante la duda se activa el NitroCasinoFix");
            }

            // Si es la version 1.3.1 o menor, activa el fix
            if(version < 132){
                // Fetch the existing instance registered by the server
                NitroCasino nitroCasino = (NitroCasino) getServer().getPluginManager().getPlugin("NitroCasino");

                // Des-registrar un evento específico (ej. InventoryClickEvent) del plugin NitroCasino
                for (RegisteredListener listener : AsyncPlayerChatEvent.getHandlerList().getRegisteredListeners()) {
                    if (listener.getPlugin().equals(nitroCasino)) {
                        AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                        plugin.getLogger().info("Evento AsyncPlayerChatEvent de NitroCasino des-registrado con éxito.");
                        getServer().getPluginManager().registerEvents(new nitroCasinoFix(nitroCasino), plugin);
                        return true;
                    }
                }
            }
            else{
                return false;
            }
        }
        return false;
    }
}
