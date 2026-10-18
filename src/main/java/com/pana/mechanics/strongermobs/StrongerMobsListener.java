package com.pana.mechanics.strongermobs;

import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class StrongerMobsListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        EntityType type = event.getEntityType();
        if (type != EntityType.WARDEN && type != EntityType.WITHER) {
            return;
        }

        // Inmunidad al Ahogamiento (Drowning)
        if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
            return;
        }

        // Inmunidad al daño por contacto directo con estalagmitas/estalactitas
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
            if (event.getEntity().getLocation().getBlock().getType() == Material.POINTED_DRIPSTONE) {
                event.setCancelled(true);
                return;
            }
        }

        // Inmunidad al caer sobre estalagmitas (daño por caída incrementado por dripstone)
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Material blockType = event.getEntity().getLocation().getBlock().getType();
            Material blockBelowType = event.getEntity().getLocation().subtract(0, 1, 0).getBlock().getType();
            if (blockType == Material.POINTED_DRIPSTONE || blockBelowType == Material.POINTED_DRIPSTONE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager().getType() == EntityType.PHANTOM && event.getEntity() instanceof org.bukkit.entity.Player player) {
            double currentHealth = player.getHealth();
            double customDamage = currentHealth * 0.20;
            // Evitamos un daño nulo o infinitesimal si al jugador le queda poquísima vida
            if (customDamage < 0.5) {
                customDamage = 0.5;
            }
            event.setDamage(customDamage);
            return;
        }

        EntityType type = event.getEntityType();
        if (type != EntityType.WARDEN && type != EntityType.WITHER) {
            return;
        }

        // Inmunidad a flechas (AbstractArrow incluye flechas normales, espectrales y tridentes)
        if (event.getDamager() instanceof AbstractArrow) {
            event.setCancelled(true);
            return;
        }

        // Inmunidad a estalactitas cayendo desde el techo (Falling Pointed Dripstone) [[1]](https://minecraft.wiki/w/Pointed_Dripstone)
        if (event.getDamager() instanceof FallingBlock fallingBlock) {
            if (fallingBlock.getBlockData().getMaterial() == Material.POINTED_DRIPSTONE) {
                event.setCancelled(true);
            }
        }
    }
}