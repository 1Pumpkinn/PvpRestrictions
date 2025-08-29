package hs.pvpRestrictions;

import org.bukkit.plugin.java.JavaPlugin;

public final class PvpRestrictions extends JavaPlugin {

    private CombatTimer combatTimer;

    @Override
    public void onEnable() {
        // Initialize combat timer first (other features depend on it)
        combatTimer = new CombatTimer(this);

        // Register all event listeners
        getServer().getPluginManager().registerEvents(combatTimer, this);
        getServer().getPluginManager().registerEvents(new DisableBundles(), this);
        getServer().getPluginManager().registerEvents(new ElytraDisabledInCombat(combatTimer), this);
        getServer().getPluginManager().registerEvents(new PotionRestrictions(), this);
        getServer().getPluginManager().registerEvents(new TippedArrowRestriction(), this);

        getLogger().info("PvP Restrictions plugin has been enabled!");
        getLogger().info("Features active:");
        getLogger().info("- 15 second combat timer");
        getLogger().info("- Bundle usage disabled");
        getLogger().info("- Elytra disabled in combat");
        getLogger().info("- Strength 2+ potions disabled");
        getLogger().info("- Restricted tipped arrows disabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("PvP Restrictions plugin has been disabled!");
    }

    public CombatTimer getCombatTimer() {
        return combatTimer;
    }

    public DisableEndPortals getEndPortalManager() {
        return endPortalManager;
    }
}