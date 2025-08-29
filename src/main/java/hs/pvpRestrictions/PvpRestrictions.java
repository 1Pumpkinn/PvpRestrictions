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
    }

    @Override
    public void onDisable() {
        getLogger().info("PvP Restrictions plugin has been disabled!");
    }

    public CombatTimer getCombatTimer() {
        return combatTimer;
    }
}