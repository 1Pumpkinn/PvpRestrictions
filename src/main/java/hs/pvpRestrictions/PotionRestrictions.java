package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionRestrictions implements Listener {

    @EventHandler
    public void onPlayerDrinkPotion(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            if (hasStrength2OrHigher(item)) {
                event.setCancelled(true);
                player.sendMessage("§cStrength 2+ potions are disabled!");
                return;
            }
        }
    }

    @EventHandler
    public void onSplashPotion(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ItemStack item = potion.getItem();

        if (hasStrength2OrHigher(item)) {
            event.setCancelled(true);

            // Notify nearby players
            for (org.bukkit.entity.LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.sendMessage("§cStrength 2+ splash potions are disabled!");
                }
            }

            // Also notify the thrower if it's a player
            if (potion.getShooter() instanceof Player) {
                Player thrower = (Player) potion.getShooter();
                thrower.sendMessage("§cStrength 2+ splash potions are disabled!");
            }
        }
    }

    @EventHandler
    public void onAreaEffectCloud(AreaEffectCloudApplyEvent event) {
        AreaEffectCloud cloud = event.getEntity();

        // Check lingering potions (area effect clouds) for Strength 2+
        for (PotionEffect effect : cloud.getCustomEffects()) {
            if (effect.getType().equals(PotionEffectType.STRENGTH) && effect.getAmplifier() >= 1) {
                event.setCancelled(true);

                // Notify affected players
                for (org.bukkit.entity.LivingEntity entity : event.getAffectedEntities()) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.sendMessage("§cStrength 2+ lingering potions are disabled!");
                    }
                }
                return;
            }
        }

        // Also check base potion type for lingering potions
        if (cloud.getBasePotionType() != null) {
            PotionType baseType = cloud.getBasePotionType();
            if (baseType.name().contains("STRONG_STRENGTH") ||
                    (baseType.name().contains("STRENGTH") && baseType.name().contains("STRONG"))) {
                event.setCancelled(true);

                for (org.bukkit.entity.LivingEntity entity : event.getAffectedEntities()) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.sendMessage("§cStrength 2+ lingering potions are disabled!");
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // Handle potions thrown from dispensers/trial chambers
        if (event.getEntity() instanceof ThrownPotion) {
            ThrownPotion potion = (ThrownPotion) event.getEntity();
            ItemStack item = potion.getItem();

            if (hasStrength2OrHigher(item)) {
                event.setCancelled(true);
                // If there's a shooter, notify them
                if (potion.getShooter() instanceof Player) {
                    Player player = (Player) potion.getShooter();
                    player.sendMessage("§cStrength 2+ potions are disabled!");
                }
            }
        }
    }

    private boolean hasStrength2OrHigher(ItemStack item) {
        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof PotionMeta)) {
            return false;
        }

        PotionMeta meta = (PotionMeta) item.getItemMeta();

        // Check custom effects first
        for (PotionEffect effect : meta.getCustomEffects()) {
            if (effect.getType().equals(PotionEffectType.STRENGTH) && effect.getAmplifier() >= 1) {
                return true;
            }
        }

        // Check base potion type
        if (meta.hasBasePotionType()) {
            PotionType baseType = meta.getBasePotionType();
            // Check for strong strength potions (Strength 2)
            if (baseType.name().contains("STRONG_STRENGTH") ||
                    (baseType.name().contains("STRENGTH") && baseType.name().contains("STRONG"))) {
                return true;
            }
        }

        return false;
    }
}