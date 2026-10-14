package com.pana.advancedenchantments;

import com.pana.advancedenchantments.items.CustomItemRegistry;
import com.pana.advancedenchantments.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private final CustomItemRegistry itemRegistry;

    private static final List<String> ITEM_OPTIONS = List.of(
            "essence_blaze",
            "essence_breeze",
            "essence_piglin",
            "essence_guardian",
            "essence_wither",
            "essence_warden",
            // Común
            "fiery_iron",
            // Raro
            "gold_fiery", "gold_airy",
            // Épico
            "geode_fiery", "geode_airy", "geode_carrot",
            // Mítico
            "redluzi", "redluzi_fiery", "redluzi_airy", "redluzi_carrot", "redluzi_aqua",
            // Legendario
            "emerald_airy", "emerald_carrot", "emerald_aqua", "emerald_death",
            // Absoluto
            "diamond_carrot", "diamond_aqua", "diamond_death", "diamond_horn",
            // Plantillas
            "common",
            "rare",
            "epic",
            "mythic",
            "legendary",
            "absolute"
    );

    public GiveCommand(CustomItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permisos para ejecutar este comando."));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso correcto: /trrus give <nombre_item> [cantidad]"));
            return true;
        }

        String targetItemName = args[1].toLowerCase();
        int amount = 1;

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) amount = 1;
            } catch (NumberFormatException e) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>La cantidad debe ser un número válido."));
                return true;
            }
        }

        ItemStack itemToGive = switch (targetItemName) {
            case "essence_blaze" -> itemRegistry.createBlazeEssence();
            case "essence_breeze" -> itemRegistry.createBreezeEssence();
            case "essence_piglin" -> itemRegistry.createPiglinEssence();
            case "essence_guardian" -> itemRegistry.createGuardianEssence();
            case "essence_wither" -> itemRegistry.createWitherEssence();
            case "essence_warden" -> itemRegistry.createWardenEssence();
            case "fiery_iron" -> itemRegistry.createFieryIron();
            case "gold_fiery", "compacto_ardiente_oro" -> itemRegistry.createGoldFieryCompact();
            case "gold_airy", "compacto_aireado_oro" -> itemRegistry.createGoldAiryCompact();
            case "geode_fiery", "geode_fiery_compact" -> itemRegistry.createGeodeFieryCompact();
            case "geode_airy", "geode_airy_compact" -> itemRegistry.createGeodeAiryCompact();
            case "geode_carrot", "geode_carrot_compact" -> itemRegistry.createGeodeCarrotCompact();
            case "redluzi", "redluzi_compact" -> itemRegistry.createRedluziCompact();
            case "redluzi_fiery", "redluzi_fiery_compact" -> itemRegistry.createRedluziFieryCompact();
            case "redluzi_airy", "redluzi_airy_compact" -> itemRegistry.createRedluziAiryCompact();
            case "redluzi_carrot", "redluzi_carrot_compact" -> itemRegistry.createRedluziCarrotCompact();
            case "redluzi_aqua", "redluzi_aqua_compact" -> itemRegistry.createRedluziAquaCompact();
            case "emerald_airy", "emerald_airy_compact" -> itemRegistry.createEmeraldAiryCompact();
            case "emerald_carrot", "emerald_carrot_compact" -> itemRegistry.createEmeraldCarrotCompact();
            case "emerald_aqua", "emerald_aqua_compact" -> itemRegistry.createEmeraldAquaCompact();
            case "emerald_death", "emerald_death_compact" -> itemRegistry.createEmeraldDeathCompact();
            case "diamond_carrot", "diamond_carrot_compact" -> itemRegistry.createDiamondCarrotCompact();
            case "diamond_aqua", "diamond_aqua_compact" -> itemRegistry.createDiamondAquaCompact();
            case "diamond_death", "diamond_death_compact" -> itemRegistry.createDiamondDeathCompact();
            case "diamond_horn", "diamond_horn_compact" -> itemRegistry.createDiamondHornCompact();
            case "common" -> itemRegistry.createTemplate(CustomItemType.TEMPLATE_COMMON);
            case "rare" -> itemRegistry.createTemplate(CustomItemType.TEMPLATE_RARE);
            case "epic" -> itemRegistry.createTemplate(CustomItemType.TEMPLATE_EPIC);
            case "mythic" -> itemRegistry.createTemplate(CustomItemType.TEMPLATE_MYTHIC);
            case "legendary" -> itemRegistry.createTemplate(CustomItemType.TEMPLATE_LEGENDARY);
            case "absolute" -> itemRegistry.createTemplate(CustomItemType.TEMPLATE_ABSOLUTE);
            default -> null;
        };

        if (itemToGive == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Ítem desconocido. Revisa el nombre ingresado."));
            return true;
        }

        itemToGive.setAmount(amount);
        player.getInventory().addItem(itemToGive);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>¡Has recibido x<amount> de <item>!",
                Placeholder.parsed("amount", String.valueOf(amount)),
                Placeholder.parsed("item", targetItemName)
        ));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.isOp()) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("give"), completions);
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            StringUtil.copyPartialMatches(args[1], ITEM_OPTIONS, completions);
            return completions;
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return List.of("1", "16", "32", "64");
        }

        return List.of();
    }
}