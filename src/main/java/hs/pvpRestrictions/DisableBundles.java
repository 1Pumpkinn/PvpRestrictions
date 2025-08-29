package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DisableBundles implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.BUNDLE) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                player.sendMessage("§cBundles are disabled on this server!");
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Check if player is trying to use bundles in inventory
        if ((currentItem != null && currentItem.getType() == Material.BUNDLE) ||
                (cursorItem != null && cursorItem.getType() == Material.BUNDLE)) {

            // Only cancel if it's a bundle interaction, not just moving bundles
            if (event.getClick().toString().contains("RIGHT")) {
                event.setCancelled(true);
                player.sendMessage("§cBundles are disabled on this server!");
            }
        }
    }
}