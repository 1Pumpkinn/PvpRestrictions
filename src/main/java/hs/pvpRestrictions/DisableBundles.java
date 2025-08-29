package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class DisableBundles implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        // Quick null check for performance
        if (item == null || item.getType() != Material.BUNDLE) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();

        // Cancel any right-click interaction with bundles
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            player.sendMessage("§cBundles are disabled on this server!");
        }

        // Also cancel left clicks to prevent any weird interactions
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            player.sendMessage("§cBundles are disabled on this server!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        ClickType clickType = event.getClick();

        // Performance optimization: quick checks first
        boolean currentIsBundle = currentItem != null && currentItem.getType() == Material.BUNDLE;
        boolean cursorIsBundle = cursorItem != null && cursorItem.getType() == Material.BUNDLE;

        if (!currentIsBundle && !cursorIsBundle) {
            return; // No bundles involved, skip
        }

        // Cancel ALL interactions with bundles
        if (currentIsBundle || cursorIsBundle) {
            event.setCancelled(true);
            player.sendMessage("§cBundles are disabled on this server!");
            return;
        }
    }
}