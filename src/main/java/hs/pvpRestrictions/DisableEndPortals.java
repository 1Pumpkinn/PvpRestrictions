package hs.pvpRestrictions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DisableEndPortals implements Listener {

    private final JavaPlugin plugin;
    private boolean endPortalsEnabled;

    public DisableEndPortals(JavaPlugin plugin) {
        this.plugin = plugin;
        // Load the saved state from config, default to false if not found
        this.endPortalsEnabled = plugin.getConfig().getBoolean("end-portals.enabled", false);
    }

    public boolean areEndPortalsEnabled() {
        return endPortalsEnabled;
    }

    public void setEndPortalsEnabled(boolean enabled) {
        this.endPortalsEnabled = enabled;
        // Save to config immediately when changed
        plugin.getConfig().set("end-portals.enabled", enabled);
        plugin.saveConfig();
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (!endPortalsEnabled && event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.sendMessage("§cEnd portals are currently disabled!");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!endPortalsEnabled && event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.sendMessage("§cEnd portals are currently disabled!");
        }
    }
}