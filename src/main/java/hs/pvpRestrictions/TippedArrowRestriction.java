package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class TippedArrowRestriction implements Listener {

    private static final Set<PotionEffectType> BANNED_EFFECTS = new HashSet<>(Arrays.asList(
            PotionEffectType.INSTANT_DAMAGE, // Harming
            PotionEffectType.SLOWNESS,
            PotionEffectType.WEAKNESS,
            PotionEffectType.POISON,
            PotionEffectType.SLOW_FALLING,
            PotionEffectType.STRENGTH
    ));

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final Player player = (Player) event.getEntity();

        // 1) Check the consumed item (the arrow stack)
        final ItemStack consumable = event.getConsumable();
        if (isTippedArrow(consumable) && isBannedArrowItem(consumable)) {
            event.setCancelled(true);
            player.sendMessage("§cTipped arrows with " + getBannedEffectName(consumable) + " are disabled!");
            return;
        }

        // 2) Also check the spawned projectile itself
        if (event.getProjectile() instanceof Arrow) {
            final Arrow arrow = (Arrow) event.getProjectile();

            // base potion type (new API)
            if (potionTypeHasBannedEffect(arrow.getBasePotionType())) {
                event.setCancelled(true);
                player.sendMessage("§cTipped arrows with " + getBannedEffectName(arrow.getBasePotionType()) + " are disabled!");
                return;
            }

            // any custom effects present
            if (arrow.hasCustomEffects()) {
                for (PotionEffect effect : arrow.getCustomEffects()) {
                    if (BANNED_EFFECTS.contains(effect.getType())) {
                        event.setCancelled(true);
                        player.sendMessage("§cTipped arrows with " + pretty(effect.getType()) + " are disabled!");
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDispenseArrow(BlockDispenseEvent event) {
        final ItemStack item = event.getItem();
        if (isTippedArrow(item) && isBannedArrowItem(item)) {
            event.setCancelled(true);
            event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 8, 8, 8)
                    .stream()
                    .filter(e -> e instanceof Player)
                    .map(e -> (Player) e)
                    .forEach(p -> p.sendMessage("§cDispenser blocked from shooting banned tipped arrow!"));
        }
    }

    // Final safety: strip banned effects if something slipped through
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        final Arrow arrow = (Arrow) event.getEntity();

        boolean removed = false;
        String lastRemoved = null;

        // remove banned base type
        if (potionTypeHasBannedEffect(arrow.getBasePotionType())) {
            // null removes base effect
            lastRemoved = getBannedEffectName(arrow.getBasePotionType());
            arrow.setBasePotionType(null);
            removed = true;
        }

        // remove any banned custom effects
        if (arrow.hasCustomEffects()) {
            for (PotionEffect effect : new ArrayList<>(arrow.getCustomEffects())) {
                if (BANNED_EFFECTS.contains(effect.getType())) {
                    arrow.removeCustomEffect(effect.getType());
                    lastRemoved = pretty(effect.getType());
                    removed = true;
                }
            }
        }

        if (removed) {
            if (event.getHitEntity() instanceof Player) {
                ((Player) event.getHitEntity()).sendMessage("§cBlocked banned tipped arrow effect: " + lastRemoved);
            }
            if (arrow.getShooter() instanceof Player) {
                ((Player) arrow.getShooter()).sendMessage("§cYour tipped arrow's banned effect was blocked: " + lastRemoved);
            }
        }
    }

    // === Helpers ===

    private boolean isTippedArrow(ItemStack item) {
        return item != null && item.getType() == Material.TIPPED_ARROW;
    }

    // Checks both base potion type and custom effects on the ItemStack (PotionMeta)
    private boolean isBannedArrowItem(ItemStack item) {
        if (!(item.getItemMeta() instanceof PotionMeta)) return false;
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        // base potion type (new API)
        if (meta.hasBasePotionType() && potionTypeHasBannedEffect(meta.getBasePotionType())) {
            return true;
        }

        // any custom effects added to the tipped arrow
        if (meta.hasCustomEffects()) {
            for (PotionEffect effect : meta.getCustomEffects()) {
                if (BANNED_EFFECTS.contains(effect.getType())) return true;
            }
        }
        return false;
    }

    private boolean potionTypeHasBannedEffect(PotionType type) {
        if (type == null) return false;
        for (PotionEffect pe : type.getPotionEffects()) {
            if (BANNED_EFFECTS.contains(pe.getType())) return true;
        }
        return false;
    }

    private String getBannedEffectName(ItemStack item) {
        if (!(item.getItemMeta() instanceof PotionMeta)) return "Unknown";
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        if (meta.hasBasePotionType()) {
            String n = getBannedEffectName(meta.getBasePotionType());
            if (n != null) return n;
        }
        if (meta.hasCustomEffects()) {
            for (PotionEffect effect : meta.getCustomEffects()) {
                if (BANNED_EFFECTS.contains(effect.getType())) {
                    return pretty(effect.getType());
                }
            }
        }
        return "Unknown";
    }

    private String getBannedEffectName(PotionType type) {
        if (type == null) return null;
        for (PotionEffect pe : type.getPotionEffects()) {
            if (BANNED_EFFECTS.contains(pe.getType())) {
                return pretty(pe.getType());
            }
        }
        return null;
    }

    // Friendly names without using deprecated getName()
    private String pretty(PotionEffectType type) {
        if (type == PotionEffectType.INSTANT_DAMAGE) return "Harming";
        if (type == PotionEffectType.SLOWNESS) return "Slowness";
        if (type == PotionEffectType.WEAKNESS) return "Weakness";
        if (type == PotionEffectType.POISON) return "Poison";
        if (type == PotionEffectType.SLOW_FALLING) return "Slow Falling";
        if (type == PotionEffectType.STRENGTH) return "Strength";
        // fallback to namespaced key -> "Instant Damage"
        String key = type.getKey().getKey().replace('_', ' ');
        return key.isEmpty() ? "Unknown" : Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }
}
