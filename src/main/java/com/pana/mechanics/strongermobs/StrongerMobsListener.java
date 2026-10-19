package com.pana.mechanics.strongermobs;

import dev.aurelium.auramobs.AuraMobs;
import dev.aurelium.auramobs.api.AuraMobsAPI;
import dev.aurelium.auramobs.listeners.MobSpawn;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StrongerMobsListener implements Listener {

    private final AuraMobs AuraMobsPlugin;

    public StrongerMobsListener(AuraMobs auraPlugin) {
        this.AuraMobsPlugin = auraPlugin;
    }

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

    private final Set<UUID> witherYaInvocaron = new HashSet<>();

    @EventHandler
    public void onWitherDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.WITHER) {
            Wither wither = (Wither) event.getEntity();

            // Si ya invocó sus esbirros antes, salimos para evitar bugs
            if (witherYaInvocaron.contains(wither.getUniqueId())) {
                return;
            }

            double vidaRestante = wither.getHealth() - event.getFinalDamage();
            org.bukkit.attribute.AttributeInstance attr = wither.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
            double vidaMaxima = (attr != null) ? attr.getValue() : 300.0;
            double mitadVida = vidaMaxima / 2.0;

            // Comprobamos si cruza la mitad de la vida
            if (wither.getHealth() > mitadVida && vidaRestante <= mitadVida) {
                if (AuraMobsAPI.isAuraMob(wither)) {

                    // Marcamos a este Wither para que no repita la invocación
                    witherYaInvocaron.add(wither.getUniqueId());

                    int level = AuraMobsAPI.getMobLevel(wither);
                    int iteraciones = level / 5;

                    // Asegurar al menos 1 iteración si el nivel es bajo pero mayor a 0
                    if (iteraciones < 1) iteraciones = 1;

                    for (int i = 0; i < iteraciones; i++) {
                        org.bukkit.Location spawnLocation = wither.getLocation().clone();

                        // Desplazamiento aleatorio o alternado seguro
                        double offsetX = (i % 2 == 0) ? 1.0 : -1.0;
                        double offsetZ = (i % 2 == 0) ? 1.0 : -1.0;
                        spawnLocation.add(offsetX, 0, offsetZ);

                        // Validar que el bloque de destino sea seguro para spawnear (opcional pero recomendado)
                        // Spawneas el esqueleto
                        WitherSkeleton skeleton = wither.getWorld().spawn(spawnLocation, WitherSkeleton.class);

                        if (skeleton != null) {
                            skeleton.setMetadata("spawned_time", new FixedMetadataValue(AuraMobsPlugin, System.currentTimeMillis()));

                            AttributeInstance scaleAttr = skeleton.getAttribute(Attribute.SCALE);
                            if (scaleAttr != null) {
                                scaleAttr.setBaseValue(0.3);
                            }

                            LivingEntity objetivoActual = wither.getTarget();
                            if (objetivoActual != null) {
                                skeleton.setTarget(objetivoActual);
                            }

                            MobSpawn manager = new MobSpawn(AuraMobsPlugin);
                            manager.changeMob(skeleton, level);
                        }
                    }
                }
            }
        }
    }

    // 1. SPAWN: Cuando el Wither aparece y hace su explosión inicial
    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER) {
            Wither wither = (Wither) event.getEntity();

            // Usamos un delay de 1 tick para asegurar que AuraMobsAPI ya lo haya registrado como AuraMob
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!wither.isValid()) return;

                    if (AuraMobsAPI.isAuraMob(wither)) {
                        int level = AuraMobsAPI.getMobLevel(wither);
                        int iteraciones = level / 5;
                        if (iteraciones < 1) iteraciones = 1;

                        for (int i = 0; i < iteraciones; i++) {
                            org.bukkit.Location spawnLocation = wither.getLocation().clone();
                            double offsetX = (i % 2 == 0) ? 1.0 : -1.0;
                            double offsetZ = (i % 2 == 0) ? 1.0 : -1.0;
                            spawnLocation.add(offsetX, 0, offsetZ);

                            WitherSkeleton skeleton = wither.getWorld().spawn(spawnLocation, WitherSkeleton.class);

                            if (skeleton != null) {
                                skeleton.setMetadata("spawned_time", new FixedMetadataValue(AuraMobsPlugin, System.currentTimeMillis()));

                                AttributeInstance scaleAttr = skeleton.getAttribute(Attribute.SCALE);
                                if (scaleAttr != null) {
                                    scaleAttr.setBaseValue(0.3);
                                }

                                LivingEntity objetivoActual = wither.getTarget();
                                if (objetivoActual != null) {
                                    skeleton.setTarget(objetivoActual);
                                }

                                MobSpawn manager = new MobSpawn(AuraMobsPlugin);
                                manager.changeMob(skeleton, level);
                            }
                        }
                    }
                }
            }.runTaskLater(AuraMobsPlugin, 1L); // Espera 1 tick a que termine de inicializarse
        }
    }

    // Evento para bloquear el daño del Wither Y el daño a jugadores durante los primeros 3 segundos
    @EventHandler
    public void onSkeletonDamage(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.WITHER_SKELETON) {
            org.bukkit.entity.Entity damager = event.getDamager();

            // 1. Bloquear siempre el daño proveniente del Wither o sus calaveras
            if (damager.getType() == EntityType.WITHER || damager.getType() == EntityType.WITHER_SKULL) {
                event.setCancelled(true);
                return;
            }

            // 2. Proteger contra jugadores durante los primeros 3 segundos (incluye ataques de barrido/sweeping edge)
            if (event.getEntity().hasMetadata("spawned_time")) {
                long spawnTime = event.getEntity().getMetadata("spawned_time").get(0).asLong();
                long tiempoActual = System.currentTimeMillis();

                // 3000 milisegundos = 3 segundos
                if ((tiempoActual - spawnTime) < 3000) {
                    if (damager instanceof org.bukkit.entity.Player || damager.getType() == EntityType.PLAYER) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}