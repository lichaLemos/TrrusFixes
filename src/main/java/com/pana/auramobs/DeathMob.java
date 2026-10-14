package com.pana.auramobs;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import dev.aurelium.auramobs.api.AuraMobsAPI;
import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.ae.enchanthandler.enchantments.AdvancedEnchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathMob implements Listener {

    private final CustomItemRegistry itemRegistry;

    public DeathMob(CustomItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (AuraMobsAPI.isAuraMob(entity)) {

            int level = AuraMobsAPI.getMobLevel(entity);

            int baseXP = event.getDroppedExp();
            AdvancedEnchantment enchantment = AEAPI.getEnchantmentInstance("experience");
            int enchantMultiplier = 1;
            if(entity.getKiller() != null){
                ItemStack mainHand = entity.getKiller().getActiveItem();
                if(AEAPI.hasCustomEnchant(enchantment.getName(), mainHand)){
                    enchantMultiplier = 1+ ((AEAPI.getEnchantLevel(enchantment.getName(), mainHand))/10);
                }
            }

            event.setDroppedExp(baseXP + (enchantMultiplier*baseXP*((level-1)/10)));

            List<ItemStack> drops = event.getDrops();
            event.getDrops().clear();

            for (ItemStack drop : drops) {
                drop.setAmount(drop.getAmount() * (level/25));
                event.getDrops().add(drop);
            }

            ItemStack essence = null;

            switch(entity.getType()){
                case BLAZE -> essence = itemRegistry.createBlazeEssence();
                case BREEZE -> essence = itemRegistry.createBreezeEssence();
                case PIGLIN_BRUTE -> essence = itemRegistry.createPiglinEssence();
                case GUARDIAN -> essence = itemRegistry.createGuardianEssence();
                case WITHER -> essence = itemRegistry.createWitherEssence();
                case WARDEN -> essence = itemRegistry.createWardenEssence();
            }

            int amount = 5;
            if(level<=40){amount = level/10;}

            if(essence != null){
                essence.setAmount(amount);
                event.getDrops().add(essence);
            }
        }
    }
}
