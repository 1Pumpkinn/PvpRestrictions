package hs.pvpRestrictions;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TippedArrowRestriction implements Listener {

    private static final Set<PotionEffectType> BANNED_EFFECTS = new HashSet<>(Arrays.asList(
            PotionEffectType.INSTANT_DAMAGE,  // Harming
            PotionEffectType.SLOWNESS,        // Slowness
            PotionEffectType.WEAKNESS,        // Weakness
            PotionEffectType.POISON,          // Poison
            PotionEffectType.SLOW_FALLING,    // Slow Falling
            PotionEffectType.STRENGTH         // Strength
    ));

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Check if arrow being shot is a tipped arrow with banned effects
        if (event.getConsumable() != null && event.getConsumable().hasItemMeta()) {
            ItemStack arrow = event.getConsumable();

            if (arrow.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) arrow.getItemMeta();

                // Check custom effects
                for (PotionEffect effect : meta.getCustomEffects()) {
                    if (BANNED_EFFECTS.contains(effect.getType())) {
                        event.setCancelled(true);
                        player.sendMessage("§cTipped arrows with " + getEffectName(effect.getType()) + " are disabled!");
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();

        // Double check - remove banned effects from arrows that somehow got through
        if (arrow.hasCustomEffects()) {
            boolean foundBannedEffect = false;

            for (PotionEffect effect : arrow.getCustomEffects()) {
                if (BANNED_EFFECTS.contains(effect.getType())) {
                    arrow.removeCustomEffect(effect.getType());
                    foundBannedEffect = true;
                }
            }

            if (foundBannedEffect && event.getHitEntity() instanceof Player) {
                Player hitPlayer = (Player) event.getHitEntity();
                hitPlayer.sendMessage("§cBanned tipped arrow effects were blocked!");
            }
        }
    }

    private String getEffectName(PotionEffectType type) {
        if (type.equals(PotionEffectType.INSTANT_DAMAGE)) return "Harming";
        if (type.equals(PotionEffectType.SLOWNESS)) return "Slowness";
        if (type.equals(PotionEffectType.WEAKNESS)) return "Weakness";
        if (type.equals(PotionEffectType.POISON)) return "Poison";
        if (type.equals(PotionEffectType.SLOW_FALLING)) return "Slow Falling";
        if (type.equals(PotionEffectType.STRENGTH)) return "Strength";
        return type.getName();
    }
}