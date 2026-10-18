package com.pana.mechanics.pickspawner;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemCreation {

    private final MiniMessage mm = MiniMessage.miniMessage();
    public final NamespacedKey customItemKey;
    public final NamespacedKey customBlockKey;

    public ItemCreation(JavaPlugin plugin){
        customItemKey = new NamespacedKey(plugin, "custom_item_id");
        customBlockKey = new NamespacedKey(plugin, "custom_block_id");
    }

    public ItemStack buildPickaxe(int level, int uses) {
        ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {

            /*

                Usos:
                    -2 = Se dan usos basándonos en el nivel
                    -1 = Es infinito

             */

            if(uses == -2){
                switch (level){
                    case 1 -> uses = 20;
                    case 2 -> uses = 80;
                }
            }

            meta.displayName(mm.deserialize("<!italic><b><gradient:#9A4BFF:#FDDEFF>ᴘɪᴄᴋsᴘᴀᴡɴᴇʀ</gradient></b>"));

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize("<!italic><gray>Un antiguo pico forjado con la"));
            lore.add(mm.deserialize("<!italic><gray>esencia concentrada de las almas."));
            lore.add(mm.deserialize("<!italic><gray>Extrae y canaliza la energía de"));
            lore.add(mm.deserialize("<!italic><gray>los spawners con facilidad extrema."));
            lore.add(mm.deserialize(""));
            String usagesText = switch (level) {
                case 1 -> "20";
                case 2 -> "80";
                case 3 -> "∞";
                default -> "Bug";
            };
            Component usages = MiniMessage.miniMessage().deserialize(
                    "<!italic> <dark_gray>▪ <white>Usos Totales: <gradient:#ff7000:#ffee00><bold><count></bold></gradient>",
                    Placeholder.parsed("count", usagesText)
            );
            lore.add(usages);
            Component soulLevel = MiniMessage.miniMessage().deserialize(
                    "<!italic> <dark_gray>▪ <white>Nivel de alma: <gradient:#9a4bff:#faabff><bold><level></bold></gradient>",
                    Placeholder.parsed("level", String.valueOf(level))
            );
            lore.add(soulLevel);
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<red><italic>⚠ Cuidado: Si se agotan los usos, el pico se destruirá."));
            meta.lore(lore);

            meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, "custom_item_pickspawner");

            if(level == 3){
                meta.setUnbreakable(true);
            }
            else{
                Damageable damageable = (Damageable) meta;
                damageable.setMaxDamage(uses);
                damageable.setDamage(uses);
            }

            item.setItemMeta(meta);
        }
        item.addUnsafeEnchantment(Enchantment.EFFICIENCY, 10);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return item;
    }

    public ItemStack changeSpawner(EntityType spawnedType){
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof BlockStateMeta blockStateMeta) {
            String spawnerName = spawnedType.name();

            Component name = MiniMessage.miniMessage().deserialize(
                    "<!italic><b><gradient:#9A4BFF:#FDDEFF>sᴘᴀᴡɴᴇʀ ᴅᴇ <name></gradient></b>",
                    Placeholder.parsed("name", spawnerName.replace("_", " "))
            );

            blockStateMeta.displayName(name);

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<!italic><gray>Un spawner lleno de energía pura."));
            lore.add(mm.deserialize("<!italic><gray>Mantiene confinadas dentro muchas almas."));
            lore.add(mm.deserialize("<!italic><gray>Ponlo en el piso y deja que poco a poco"));
            lore.add(mm.deserialize("<!italic><gray>libere todas las almas atrapadas en el."));
            lore.add(mm.deserialize(""));
            blockStateMeta.lore(lore);

            blockStateMeta.getPersistentDataContainer().set(customBlockKey, PersistentDataType.STRING, "custom_block_spawner");

            // 3. Obtenemos el BlockState del Spawner e indicamos el tipo de mob
            if (blockStateMeta.getBlockState() instanceof CreatureSpawner spawner) {
                spawner.setSpawnedType(spawnedType);
                // Muy importante: Guardamos los cambios de vuelta en el meta
                blockStateMeta.setBlockState(spawner);
            }

            item.setItemMeta(blockStateMeta);
        }

        return item;
    }

    public ItemStack staySpawner(EntityType spawnedType){
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof BlockStateMeta blockStateMeta) {

            String spawnerName = spawnedType.name();
            Component name = MiniMessage.miniMessage().deserialize(
                    "<!italic><b><gradient:#9A4BFF:#FDDEFF>sᴘᴀᴡɴᴇʀ ᴅᴇ <name></gradient></b>",
                    Placeholder.parsed("name", spawnerName.replace("_", " "))
            );

            blockStateMeta.displayName(name);

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<!italic><gray>Un spawner vacío."));
            lore.add(mm.deserialize("<!italic><gray>Mantiene confinadas dentro muchas almas."));
            lore.add(mm.deserialize("<!italic><gray>Pero en este estado, no las puede liberar."));
            lore.add(mm.deserialize("<!italic><gray>Para ello, debes cargarlo."));
            lore.add(mm.deserialize(""));
            blockStateMeta.lore(lore);

            // 3. Obtenemos el BlockState del Spawner e indicamos el tipo de mob
            if (blockStateMeta.getBlockState() instanceof CreatureSpawner spawner) {
                spawner.setSpawnedType(spawnedType);
                // Muy importante: Guardamos los cambios de vuelta en el meta
                blockStateMeta.setBlockState(spawner);
            }

            item.setItemMeta(blockStateMeta);
        }

        return item;
    }


}
