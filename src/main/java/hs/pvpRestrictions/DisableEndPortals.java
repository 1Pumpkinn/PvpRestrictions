package hs.pvpRestrictions;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DisableEndPortals implements Listener {

    private final JavaPlugin plugin;
    private boolean endPortalsEnabled;

    public DisableEndPortals(JavaPlugin plugin) {
        this.plugin = plugin;
        // Load saved state from config, default false
        this.endPortalsEnabled = plugin.getConfig().getBoolean("end-portals.enabled", false);
    }

    public boolean areEndPortalsEnabled() {
        return endPortalsEnabled;
    }

    public void setEndPortalsEnabled(boolean enabled) {
        this.endPortalsEnabled = enabled;
        plugin.getConfig().set("end-portals.enabled", enabled);
        plugin.saveConfig();
    }

    // Block player teleports into the End
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!endPortalsEnabled &&
                (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL ||
                        event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {

            if (event.getTo() != null &&
                    event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {

                Player player = event.getPlayer();
                event.setCancelled(true);
                player.sendMessage("§cEnd access is currently disabled!");
            }
        }
    }

    // Block entities going into the End
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (!endPortalsEnabled &&
                event.getTo() != null &&
                event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {

            event.setCancelled(true);
        }
    }
}
