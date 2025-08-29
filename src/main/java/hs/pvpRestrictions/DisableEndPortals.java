package hs.pvpRestrictions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DisableEndPortals implements Listener {

    private boolean endPortalsEnabled = false; // Default: disabled

    public boolean areEndPortalsEnabled() {
        return endPortalsEnabled;
    }

    public void setEndPortalsEnabled(boolean enabled) {
        this.endPortalsEnabled = enabled;
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