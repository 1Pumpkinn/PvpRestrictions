package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ElytraDisabledInCombat implements Listener {

    private final CombatTimer combatTimer;

    public ElytraDisabledInCombat(CombatTimer combatTimer) {
        this.combatTimer = combatTimer;
    }

    @EventHandler
    public void onElytraToggle(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (combatTimer.isInCombat(player) && event.isGliding()) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot use elytra while in combat!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Prevent equipping elytra while in combat
        if (item != null && item.getType() == Material.ELYTRA && combatTimer.isInCombat(player)) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot equip elytra while in combat!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (!combatTimer.isInCombat(player)) {
            return;
        }

        // Check if player is trying to move elytra to chestplate slot
        if (event.getSlot() == 38) { // Chestplate slot
            ItemStack item = event.getCursor();
            if (item != null && item.getType() == Material.ELYTRA) {
                event.setCancelled(true);
                player.sendMessage("§cYou cannot equip elytra while in combat!");
            }
        }

        // Check if clicking on elytra in chestplate slot
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() == Material.ELYTRA && event.getSlot() == 38) {
            // Allow removing elytra, just not equipping
            return;
        }
    }
}