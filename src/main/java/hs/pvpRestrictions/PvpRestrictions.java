package hs.pvpRestrictions;

import hs.pvpRestrictions.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class PvpRestrictions extends JavaPlugin {

    private CombatTimer combatTimer;
    private DisableEndPortals endPortalManager;
    private TrustManager trustManager;

    @Override
    public void onEnable() {
        // Initialize trust manager first
        trustManager = new TrustManager(this);

        // Initialize combat timer (depends on trust manager)
        combatTimer = new CombatTimer(this);

        // Initialize end portal manager
        endPortalManager = new DisableEndPortals();

        // Register all event listeners
        getServer().getPluginManager().registerEvents(combatTimer, this);
        getServer().getPluginManager().registerEvents(new DisableBundles(), this);
        getServer().getPluginManager().registerEvents(new ElytraDisabledInCombat(combatTimer), this);
        getServer().getPluginManager().registerEvents(new PotionRestrictions(), this);
        getServer().getPluginManager().registerEvents(new TippedArrowRestriction(), this);
        getServer().getPluginManager().registerEvents(endPortalManager, this);

        // Register commands
        getCommand("endportals").setExecutor(new EndPortalsOpened(endPortalManager));
        getCommand("trust").setExecutor(new TrustCommand(trustManager));
        getCommand("untrust").setExecutor(new UntrustCommand(trustManager));
        getCommand("trustaccept").setExecutor(new TrustAcceptCommand(trustManager));
        getCommand("trustdeny").setExecutor(new TrustDenyCommand(trustManager));
        getCommand("trustlist").setExecutor(new TrustListCommand(trustManager));

        getLogger().info("PvP Restrictions plugin has been enabled!");
        getLogger().info("Features active:");
        getLogger().info("- 15 second combat timer with trust system");
        getLogger().info("- Bundle usage disabled");
        getLogger().info("- Elytra disabled in combat");
        getLogger().info("- Strength 2+ potions disabled");
        getLogger().info("- Restricted tipped arrows disabled");
        getLogger().info("- End portals can be toggled with /endportals");
        getLogger().info("- Trust system: /trust, /untrust, /trustlist, /trustaccept, /trustdeny");
    }

    @Override
    public void onDisable() {
        // Save trust data before shutting down
        if (trustManager != null) {
            trustManager.saveAllData();
        }
        getLogger().info("PvP Restrictions plugin has been disabled!");
    }

    public CombatTimer getCombatTimer() {
        return combatTimer;
    }

    public DisableEndPortals getEndPortalManager() {
        return endPortalManager;
    }

    public TrustManager getTrustManager() {
        return trustManager;
    }
}