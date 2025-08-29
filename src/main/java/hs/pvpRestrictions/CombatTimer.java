package hs.pvpRestrictions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTimer implements Listener {

    private final PvpRestrictions plugin;
    private final Map<UUID, Long> combatPlayers = new HashMap<>();
    private static final long COMBAT_TIME = 15000; // 15 seconds in milliseconds

    public CombatTimer(PvpRestrictions plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Put both players in combat
        putInCombat(victim);
        putInCombat(attacker);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (isInCombat(player)) {
            // Kill player for combat logging
            player.setHealth(0);
            Bukkit.broadcastMessage("§c" + player.getName() + " has been killed for combat logging!");
        }

        // Remove from combat tracker
        combatPlayers.remove(player.getUniqueId());
    }

    public void putInCombat(Player player) {
        combatPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage("§cYou are now in combat for 15 seconds!");
    }

    public boolean isInCombat(Player player) {
        Long combatTime = combatPlayers.get(player.getUniqueId());
        if (combatTime == null) {
            return false;
        }

        if (System.currentTimeMillis() - combatTime > COMBAT_TIME) {
            combatPlayers.remove(player.getUniqueId());
            player.sendMessage("§aYou are no longer in combat.");
            return false;
        }

        return true;
    }

    public long getRemainingCombatTime(Player player) {
        Long combatTime = combatPlayers.get(player.getUniqueId());
        if (combatTime == null) {
            return 0;
        }

        long remaining = COMBAT_TIME - (System.currentTimeMillis() - combatTime);
        return Math.max(0, remaining);
    }

    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Clean up expired combat timers
                combatPlayers.entrySet().removeIf(entry -> {
                    if (System.currentTimeMillis() - entry.getValue() > COMBAT_TIME) {
                        Player player = Bukkit.getPlayer(entry.getKey());
                        if (player != null && player.isOnline()) {
                            player.sendMessage("§aYou are no longer in combat.");
                        }
                        return true;
                    }
                    return false;
                });
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }
}