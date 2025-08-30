package hs.pvpRestrictions;

import hs.pvpRestrictions.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class PvpRestrictions extends JavaPlugin {

    private DisableEndPortals endPortalManager;

    @Override
    public void onEnable() {
        // Load/create config file
        saveDefaultConfig();

        // Initialize end portal manager with this plugin instance
        endPortalManager = new DisableEndPortals(this);

        // Register all event listeners
        getServer().getPluginManager().registerEvents(new DisableBundles(), this);
        getServer().getPluginManager().registerEvents(new PotionRestrictions(), this);
        getServer().getPluginManager().registerEvents(new TippedArrowRestriction(), this);
        getServer().getPluginManager().registerEvents(endPortalManager, this);

        // Register commands
        getCommand("endportals").setExecutor(new EndPortalsOpened(endPortalManager));

        getLogger().info("PvP Restrictions plugin has been enabled!");
        getLogger().info("End portals are " + (endPortalManager.areEndPortalsEnabled() ? "ENABLED" : "DISABLED"));
    }

    @Override
    public void onDisable() {
        // Config is automatically saved when setEndPortalsEnabled() is called
        // But we can also save it here as a safety measure
        if (endPortalManager != null) {
            getConfig().set("end-portals.enabled", endPortalManager.areEndPortalsEnabled());
            saveConfig();
        }

        getLogger().info("PvP Restrictions plugin has been disabled!");
    }

    public DisableEndPortals getEndPortalManager() {
        return endPortalManager;
    }
}