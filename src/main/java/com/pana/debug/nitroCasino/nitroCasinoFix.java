package com.pana.debug.nitroCasino;

import dev.redbill.nitrocasino.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class nitroCasinoFix implements Listener {
    private final NitroCasino plugin;
    private static final Map<UUID, Double> mobRaceBets = new HashMap();

    public nitroCasinoFix(NitroCasino plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onChat(AsyncPlayerChatEvent event) {
        String cleanMessage = ChatColor.stripColor(event.getMessage()).trim();
        boolean amount = processChatInput(event.getPlayer(), cleanMessage);

        if (amount) {
            event.setCancelled(true);
        }
    }

    public boolean processChatInput(Player player, String message) {
        UUID uuid = player.getUniqueId();
        if (message.equalsIgnoreCase("accept") || message.equalsIgnoreCase("decline")) {
            for(Location cloc : this.plugin.getCoinflipManager().getCasinoLocations()) {
                CoinflipSession csession = this.plugin.getCoinflipManager().getSession(cloc);
                if (csession != null && csession.hasPendingInvite(uuid)) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        if (message.equalsIgnoreCase("accept")) {
                            csession.acceptInvite(player);
                        } else {
                            csession.declineInvite(player);
                        }

                    });
                    return true;
                }
            }
        }

        if (CasinoGUI.getPendingBets().containsKey(uuid)) {
            String betType = (String)CasinoGUI.getPendingBets().remove(uuid);
            if (message.equalsIgnoreCase("cancel")) {
                this.plugin.sendMessage(player, "bet-cancelled", new String[0]);
                clearPendingState(uuid);
                return true;
            }

            try {
                if (!betType.equals("COINFLIP_OPPONENT") && !betType.equals("MOBRACE_ANIMAL")) {
                    double amount = Double.parseDouble(message);
                    if (amount <= (double)0.0F) {
                        this.plugin.sendMessage(player, "positive-amount", new String[0]);
                        CasinoGUI.getPendingBets().put(uuid, betType);
                        return true;
                    }

                    if (!NitroCasino.getEconomy().has(player, amount)) {
                        this.plugin.sendMessage(player, "not-enough-money", new String[0]);
                        return true;
                    }

                    double minBet = this.getMinBet(uuid);
                    if (amount < minBet) {
                        this.plugin.sendMessage(player, "min-bet", new String[]{"{min}", String.valueOf(minBet)});
                        CasinoGUI.getPendingBets().put(uuid, betType);
                        return true;
                    }

                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        NitroCasino.getEconomy().withdrawPlayer(player, amount);
                        this.dispatchBet(player, uuid, betType, amount);
                    });
                    if (betType.equals("COINFLIP_CHALLENGE")) {
                        Location loc = (Location)CoinflipGUI.getPendingLocations().remove(uuid);
                        CoinflipSession session = this.plugin.getCoinflipManager().getSession(loc);
                        if (session != null) {
                            return true;
                        }
                    }

                    return true;
                }

                if (betType.equals("COINFLIP_OPPONENT")) {
                    Location loc = (Location)CoinflipGUI.getPendingLocations().remove(uuid);
                    CoinflipSession session = this.plugin.getCoinflipManager().getSession(loc);
                    if (session != null) {
                        if (message.equalsIgnoreCase("none") || message.equalsIgnoreCase("public") || message.equalsIgnoreCase("-")) {
                            this.plugin.sendMessage(player, "coinflip-public-created", new String[0]);
                            return true;
                        }

                        Player opponent = Bukkit.getPlayer(message);
                        if (opponent == null || !opponent.isOnline()) {
                            this.plugin.sendMessage(player, "coinflip-opponent-not-found", new String[0]);
                            return true;
                        }

                        session.invitePlayer(player, opponent, session.getBetAmount());
                    }

                    return true;
                }
            } catch (NumberFormatException var11) {
                this.plugin.getSoundManager().play(player, "invalid-action");
                this.plugin.sendMessage(player, "invalid-amount", new String[0]);
                CasinoGUI.getPendingBets().put(uuid, betType);
                return true;
            }
        }

        return false;
    }

    private double getMinBet(UUID uuid) {
        if (CasinoGUI.getPendingLocations().containsKey(uuid)) {
            return this.plugin.getRouletteConfig().getDouble("min-bet", (double)1.0F);
        } else if (CrashGUI.getPendingLocations().containsKey(uuid)) {
            return this.plugin.getCrashConfig().getDouble("min-bet", (double)100.0F);
        } else if (JackpotGUI.getPendingLocations().containsKey(uuid)) {
            return this.plugin.getJackpotConfig().getDouble("min-bet", (double)100.0F);
        } else if (CoinflipGUI.getPendingLocations().containsKey(uuid)) {
            return this.plugin.getCoinflipConfig().getDouble("min-bet", (double)100.0F);
        } else {
            return MobRaceGUI.getPendingLocations().containsKey(uuid) ? this.plugin.getMobRaceConfig().getDouble("min-bet", (double)100.0F) : (double)0.0F;
        }
    }

    private void dispatchBet(Player player, UUID uuid, String betType, double amount) {
        if (betType.equals("CRASH_BET")) {
            Location cLoc = (Location)CrashGUI.getPendingLocations().remove(uuid);
            if (cLoc != null) {
                CrashSession session = this.plugin.getCrashManager().getSession(cLoc);
                if (session != null) {
                    session.addBet(uuid, amount);
                    this.plugin.sendMessage(player, "bet-placed", new String[]{"{amount}", String.valueOf(amount), "{betType}", "CRASH"});
                }
            }
        } else if (betType.equals("JACKPOT_BET")) {
            Location jLoc = (Location)JackpotGUI.getPendingLocations().remove(uuid);
            if (jLoc != null) {
                JackpotSession session = this.plugin.getJackpotManager().getSession(jLoc);
                if (session != null) {
                    session.addBet(uuid, amount);
                }
            }
        } else if (betType.equals("COINFLIP_BET")) {
            Location cfLoc = (Location)CoinflipGUI.getPendingLocations().remove(uuid);
            if (cfLoc != null) {
                CoinflipSession session = this.plugin.getCoinflipManager().getSession(cfLoc);
                if (session != null) {
                    session.createPublicGame(player, amount);
                    this.plugin.sendMessage(player, "coinflip-public-created", new String[0]);
                }
            }
        } else if (betType.equals("COINFLIP_JOIN_BET")) {
            Location cfLoc = (Location)CoinflipGUI.getPendingLocations().remove(uuid);
            if (cfLoc != null) {
                CoinflipSession session = this.plugin.getCoinflipManager().getSession(cfLoc);
                if (session != null) {
                    session.completeJoinRequest(player, amount);
                }
            }
        } else if (betType.startsWith("MOBRACE_BET:")) {
            Location mrLoc = (Location)MobRaceGUI.getPendingLocations().remove(uuid);
            if (mrLoc != null) {
                String animal = betType.split(":")[1];
                MobRaceSession session = this.plugin.getMobRaceManager().getSession(mrLoc);
                if (session != null) {
                    session.addBet(player, amount, animal);
                    this.plugin.getMobRaceGUI().openRaceGUI(player, session);
                }
            }
        } else {
            Location rLoc = (Location)CasinoGUI.getPendingLocations().remove(uuid);
            if (rLoc != null) {
                CasinoSession session = this.plugin.getCasinoManager().getSession(rLoc);
                if (session != null) {
                    if (!session.isAcceptingBets()) {
                        NitroCasino.getEconomy().depositPlayer(player, amount);
                        this.plugin.sendMessage(player, "bets-closed", new String[0]);
                        return;
                    }

                    session.addBet(uuid, amount, betType);
                    this.plugin.sendMessage(player, "bet-placed", new String[]{"{amount}", String.valueOf(amount), "{betType}", betType});
                }
            }
        }

    }

    public static void clearPendingState(UUID uuid) {
        CasinoGUI.getPendingLocations().remove(uuid);
        CrashGUI.getPendingLocations().remove(uuid);
        JackpotGUI.getPendingLocations().remove(uuid);
        CoinflipGUI.getPendingLocations().remove(uuid);
        MobRaceGUI.getPendingLocations().remove(uuid);
        mobRaceBets.remove(uuid);
    }

    public static Map<UUID, Double> getMobRaceBets() {
        return mobRaceBets;
    }
}