package hs.pvpRestrictions;

import hs.pvpRestrictions.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class PvpRestrictions extends JavaPlugin {

    private DisableEndPortals endPortalManager;

    @Override
    public void onEnable() {

        // Initialize end portal manager
        endPortalManager = new DisableEndPortals();

        // Register all event listeners
        getServer().getPluginManager().registerEvents(new DisableBundles(), this);
        getServer().getPluginManager().registerEvents(new PotionRestrictions(), this);
        getServer().getPluginManager().registerEvents(new TippedArrowRestriction(), this);
        getServer().getPluginManager().registerEvents(endPortalManager, this);

        // Register commands
        getCommand("endportals").setExecutor(new EndPortalsOpened(endPortalManager));


        getLogger().info("PvP Restrictions plugin has been enabled!");
    }

    @Override
    public void onDisable() {


        getLogger().info("PvP Restrictions plugin has been disabled!");
    }


    public DisableEndPortals getEndPortalManager() {
        return endPortalManager;
    }

}