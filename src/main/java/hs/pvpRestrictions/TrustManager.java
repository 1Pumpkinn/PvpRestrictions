package hs.pvpRestrictions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrustManager {

    private final PvpRestrictions plugin;
    private final Map<UUID, Set<UUID>> trustRelations = new HashMap<>();
    private final Map<UUID, UUID> pendingTrustRequests = new HashMap<>();
    private final File dataFile;
    private final FileConfiguration dataConfig;

    public TrustManager(PvpRestrictions plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "trust.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        loadTrustData();
    }

    /**
     * Sends a trust request to another player with clickable chat
     */
    public void sendTrustRequest(Player requester, Player target) {
        // Validation checks
        if (requester.equals(target)) {
            requester.sendMessage(Component.text("❌ You cannot trust yourself!")
                    .color(NamedTextColor.RED));
            return;
        }

        if (isTrusted(requester, target)) {
            requester.sendMessage(Component.text("❌ You already trust " + target.getName() + "!")
                    .color(NamedTextColor.RED));
            return;
        }

        // Check if there's already a pending request FROM this requester TO target
        if (pendingTrustRequests.containsKey(target.getUniqueId()) &&
                pendingTrustRequests.get(target.getUniqueId()).equals(requester.getUniqueId())) {
            requester.sendMessage(Component.text("❌ You already have a pending trust request to " + target.getName() + "!")
                    .color(NamedTextColor.RED));
            return;
        }

        // Check if target already has a pending request FROM someone else
        if (pendingTrustRequests.containsKey(target.getUniqueId())) {
            UUID existingRequesterUUID = pendingTrustRequests.get(target.getUniqueId());
            Player existingRequester = Bukkit.getPlayer(existingRequesterUUID);
            String existingRequesterName = existingRequester != null ? existingRequester.getName() : "Unknown";

            requester.sendMessage(Component.text("❌ " + target.getName() + " already has a pending request from " + existingRequesterName + "!")
                    .color(NamedTextColor.RED));
            return;
        }

        // Store the pending request
        pendingTrustRequests.put(target.getUniqueId(), requester.getUniqueId());

        // Send confirmation to requester
        requester.sendMessage(Component.text("🤝 Trust request sent to " + target.getName() + "!")
                .color(NamedTextColor.GREEN));

        // Create clickable accept and deny buttons
        Component acceptButton = Component.text("[ACCEPT]")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.BOLD, true)
                .clickEvent(ClickEvent.runCommand("/trustaccept"))
                .hoverEvent(Component.text("Click to accept the trust request").color(NamedTextColor.GREEN));

        Component denyButton = Component.text("[DENY]")
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.BOLD, true)
                .clickEvent(ClickEvent.runCommand("/trustdeny"))
                .hoverEvent(Component.text("Click to deny the trust request").color(NamedTextColor.RED));

        // Send the trust request message with clickable buttons
        target.sendMessage(Component.text("═══════════════════════════════════")
                .color(NamedTextColor.GOLD));
        target.sendMessage(Component.text("🤝 TRUST REQUEST 🤝")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));
        target.sendMessage(Component.text("═══════════════════════════════════")
                .color(NamedTextColor.GOLD));
        target.sendMessage(Component.text(requester.getName() + " wants to become allies!")
                .color(NamedTextColor.YELLOW));
        target.sendMessage(Component.text(""));
        target.sendMessage(Component.text("Allied players are protected from:")
                .color(NamedTextColor.GRAY));
        target.sendMessage(Component.text("• PvP damage from each other")
                .color(NamedTextColor.DARK_GRAY));
        target.sendMessage(Component.text("• Combat timer activation")
                .color(NamedTextColor.DARK_GRAY));
        target.sendMessage(Component.text("• Other plugin restrictions")
                .color(NamedTextColor.DARK_GRAY));
        target.sendMessage(Component.text(""));
        target.sendMessage(acceptButton.append(Component.text("  ").color(NamedTextColor.WHITE)).append(denyButton));
        target.sendMessage(Component.text(""));
        target.sendMessage(Component.text("Or type: /trustaccept or /trustdeny")
                .color(NamedTextColor.GRAY));
        target.sendMessage(Component.text("Request expires in 60 seconds")
                .color(NamedTextColor.DARK_GRAY));
        target.sendMessage(Component.text("═══════════════════════════════════")
                .color(NamedTextColor.GOLD));

        // Auto-remove request after 60 seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingTrustRequests.containsKey(target.getUniqueId()) &&
                    pendingTrustRequests.get(target.getUniqueId()).equals(requester.getUniqueId())) {
                pendingTrustRequests.remove(target.getUniqueId());
                if (target.isOnline()) {
                    target.sendMessage(Component.text("⏰ Trust request from " + requester.getName() + " has expired.")
                            .color(NamedTextColor.GRAY));
                }
                if (requester.isOnline()) {
                    requester.sendMessage(Component.text("⏰ Your trust request to " + target.getName() + " has expired.")
                            .color(NamedTextColor.GRAY));
                }
            }
        }, 1200L); // 60 seconds
    }

    /**
     * Accept a trust request
     */
    public void acceptTrustRequest(Player accepter) {
        UUID requesterUUID = pendingTrustRequests.get(accepter.getUniqueId());
        if (requesterUUID == null) {
            accepter.sendMessage(Component.text("❌ You don't have any pending trust requests!")
                    .color(NamedTextColor.RED));
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null || !requester.isOnline()) {
            accepter.sendMessage(Component.text("❌ The player who sent the trust request is no longer online!")
                    .color(NamedTextColor.RED));
            pendingTrustRequests.remove(accepter.getUniqueId());
            return;
        }

        // Remove the pending request
        pendingTrustRequests.remove(accepter.getUniqueId());

        // Add mutual trust
        addTrust(requester.getUniqueId(), accepter.getUniqueId());
        addTrust(accepter.getUniqueId(), requester.getUniqueId());

        saveTrustData();

        // Send success messages
        Component allyMessage = Component.text("✅ ALLIANCE FORMED! ✅")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.BOLD, true);

        requester.sendMessage(allyMessage);
        requester.sendMessage(Component.text("🤝 " + accepter.getName() + " accepted your trust request!")
                .color(NamedTextColor.GREEN));
        requester.sendMessage(Component.text("You are now allies and won't trigger combat with each other!")
                .color(NamedTextColor.YELLOW));

        accepter.sendMessage(allyMessage);
        accepter.sendMessage(Component.text("🤝 You accepted " + requester.getName() + "'s trust request!")
                .color(NamedTextColor.GREEN));
        accepter.sendMessage(Component.text("You are now allies and won't trigger combat with each other!")
                .color(NamedTextColor.YELLOW));

        plugin.getLogger().info("Alliance formed between " + requester.getName() + " and " + accepter.getName());
    }

    /**
     * Deny a trust request
     */
    public void denyTrustRequest(Player denier) {
        UUID requesterUUID = pendingTrustRequests.get(denier.getUniqueId());
        if (requesterUUID == null) {
            denier.sendMessage(Component.text("❌ You don't have any pending trust requests!")
                    .color(NamedTextColor.RED));
            return;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        pendingTrustRequests.remove(denier.getUniqueId());

        denier.sendMessage(Component.text("❌ Trust request denied.")
                .color(NamedTextColor.RED));

        if (requester != null && requester.isOnline()) {
            requester.sendMessage(Component.text("❌ " + denier.getName() + " denied your trust request.")
                    .color(NamedTextColor.RED));
        }
    }

    /**
     * Remove trust between two players (allows either player to break the alliance)
     */
    public void removeTrust(Player remover, Player target) {
        if (!isTrusted(remover, target)) {
            remover.sendMessage(Component.text("❌ You are not allied with " + target.getName() + "!")
                    .color(NamedTextColor.RED));
            return;
        }

        // Remove mutual trust
        removeTrustRelation(remover.getUniqueId(), target.getUniqueId());
        removeTrustRelation(target.getUniqueId(), remover.getUniqueId());

        saveTrustData();

        remover.sendMessage(Component.text("💔 Alliance with " + target.getName() + " has been broken.")
                .color(NamedTextColor.YELLOW));

        if (target.isOnline()) {
            target.sendMessage(Component.text("💔 " + remover.getName() + " has broken your alliance.")
                    .color(NamedTextColor.YELLOW));
        }

        plugin.getLogger().info("Alliance broken between " + remover.getName() + " and " + target.getName());
    }

    /**
     * Check if two players trust each other (mutual trust required)
     * This is the main method used by combat timer to check protection
     */
    public boolean isTrusted(Player player1, Player player2) {
        if (player1 == null || player2 == null) return false;
        if (player1.equals(player2)) return false;

        return isTrusted(player1.getUniqueId(), player2.getUniqueId()) &&
                isTrusted(player2.getUniqueId(), player1.getUniqueId());
    }

    /**
     * Get list of trusted players for a player
     */
    public List<String> getTrustedPlayers(Player player) {
        Set<UUID> trusted = trustRelations.get(player.getUniqueId());
        List<String> names = new ArrayList<>();

        if (trusted != null) {
            for (UUID uuid : trusted) {
                Player trustedPlayer = Bukkit.getPlayer(uuid);
                if (trustedPlayer != null) {
                    // Verify mutual trust
                    if (isTrusted(player, trustedPlayer)) {
                        names.add(trustedPlayer.getName());
                    }
                } else {
                    // Try to get offline player name
                    String name = Bukkit.getOfflinePlayer(uuid).getName();
                    if (name != null) {
                        names.add(name + " (offline)");
                    }
                }
            }
        }

        names.sort(String::compareToIgnoreCase);
        return names;
    }

    // Private helper methods

    private boolean isTrusted(UUID player1, UUID player2) {
        Set<UUID> trusted = trustRelations.get(player1);
        return trusted != null && trusted.contains(player2);
    }

    private void addTrust(UUID player1, UUID player2) {
        Set<UUID> trusted = trustRelations.computeIfAbsent(player1, k -> new HashSet<>());
        trusted.add(player2);
    }

    private void removeTrustRelation(UUID player1, UUID player2) {
        Set<UUID> trusted = trustRelations.get(player1);
        if (trusted != null) {
            trusted.remove(player2);
            if (trusted.isEmpty()) {
                trustRelations.remove(player1);
            }
        }
    }

    private void loadTrustData() {
        try {
            if (dataFile.exists()) {
                int loadedRelations = 0;
                for (String uuidString : dataConfig.getKeys(false)) {
                    try {
                        UUID playerUUID = UUID.fromString(uuidString);
                        List<String> trustedList = dataConfig.getStringList(uuidString);
                        Set<UUID> trustedSet = new HashSet<>();

                        for (String trustedUuidString : trustedList) {
                            try {
                                trustedSet.add(UUID.fromString(trustedUuidString));
                            } catch (IllegalArgumentException e) {
                                plugin.getLogger().warning("Invalid trusted UUID: " + trustedUuidString);
                            }
                        }

                        if (!trustedSet.isEmpty()) {
                            trustRelations.put(playerUUID, trustedSet);
                            loadedRelations++;
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid player UUID in trust.yml: " + uuidString);
                    }
                }
                plugin.getLogger().info("Loaded " + loadedRelations + " trust relationships from file.");
            } else {
                plugin.getLogger().info("No trust data file found, starting fresh.");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading trust data: " + e.getMessage());
        }
    }

    private void saveTrustData() {
        try {
            // Clear existing data
            for (String key : dataConfig.getKeys(false)) {
                dataConfig.set(key, null);
            }

            // Save current data
            int savedRelations = 0;
            for (Map.Entry<UUID, Set<UUID>> entry : trustRelations.entrySet()) {
                List<String> trustedList = new ArrayList<>();
                for (UUID trustedUUID : entry.getValue()) {
                    trustedList.add(trustedUUID.toString());
                }
                dataConfig.set(entry.getKey().toString(), trustedList);
                savedRelations++;
            }

            dataConfig.save(dataFile);
            plugin.getLogger().info("Saved " + savedRelations + " trust relationships to file.");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save trust data: " + e.getMessage());
        }
    }

    public void saveAllData() {
        saveTrustData();
    }

    public boolean hasPendingRequest(Player player) {
        return pendingTrustRequests.containsKey(player.getUniqueId());
    }
}