package com.pana.citizens;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCClickListener implements Listener {

    private final String TARGET_WORLD = "summer_trade";
    private final String PREFIX = "shop_";

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        handleNPCClick(event.getClicker(), event.getNPC());
    }

    @EventHandler
    public void onNPCLeftClick(NPCLeftClickEvent event) {
        handleNPCClick(event.getClicker(), event.getNPC());
    }

    private void handleNPCClick(Player player, NPC npc) {
        // 1. Validar que el jugador está en el mundo objetivo
        if (!player.getWorld().getName().equalsIgnoreCase(TARGET_WORLD)) {
            return;
        }

        // 2. Limpiar el nombre del NPC eliminando códigos de formato o color
        String rawName = npc.getName();
        String cleanName = ChatColor.stripColor(rawName).trim();

        // 3. Comprobar si el nombre comienza con 'shop_'
        if (cleanName.toLowerCase().startsWith(PREFIX)) {
            // Extraer la categoría (ejemplo: "shop_armas" -> "armas")
            String category = cleanName.substring(PREFIX.length());

            if (category.isEmpty()) {
                return;
            }

            // 4. Abrir la categoría directamente llamando a la API de ShopGUI+
            try {
                ShopGuiPlusApi.openShop(player, category, 1);
            } catch (Exception e) {
                player.sendMessage("§cError al intentar abrir la categoría de tienda: " + category);
                e.printStackTrace();
            }
        }
    }
}