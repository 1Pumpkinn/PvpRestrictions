package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionRestrictions implements Listener {

    @EventHandler
    public void onPlayerDrinkPotion(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            if (item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();

                // Check custom effects
                for (PotionEffect effect : meta.getCustomEffects()) {
                    if (effect.getType().equals(PotionEffectType.STRENGTH) && effect.getAmplifier() >= 1) {
                        event.setCancelled(true);
                        player.sendMessage("§cStrength 2+ potions are disabled!");
                        return;
                    }
                }

                // Check base potion effects
                if (meta.hasBasePotionType()) {
                    // This would need to be checked based on your server's potion system
                    // As different versions handle this differently
                }
            }
        }
    }

    @EventHandler
    public void onSplashPotion(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ItemStack item = potion.getItem();

        if (item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();

            // Check for Strength 2+ in splash potions
            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getType().equals(PotionEffectType.STRENGTH) && effect.getAmplifier() >= 1) {
                    event.setCancelled(true);

                    // Notify nearby players
                    for (Player player : event.getAffectedEntities()) {
                        if (player instanceof Player) {
                            player.sendMessage("§cStrength 2+ splash potions are disabled!");
                        }
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onAreaEffectCloud(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();

        // Check lingering potions (area effect clouds)
        for (PotionEffect effect : cloud.getCustomEffects()) {
            if (effect.getType().equals(PotionEffectType.STRENGTH) && effect.getAmplifier() >= 1) {
                event.setCancelled(true);

                // Notify affected players
                for (Player player : event.getAffectedEntities()) {
                    if (player instanceof Player) {
                        player.sendMessage("§cStrength 2+ lingering potions are disabled!");
                    }
                }
                return;
            }
        }
    }
}