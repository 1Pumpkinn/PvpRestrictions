package hs.pvpRestrictions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DisableBundles implements Listener {

    // All bundle types including colored ones
    private static final Set<Material> ALL_BUNDLES = new HashSet<>(Arrays.asList(
            Material.BUNDLE,
            Material.WHITE_BUNDLE,
            Material.ORANGE_BUNDLE,
            Material.MAGENTA_BUNDLE,
            Material.LIGHT_BLUE_BUNDLE,
            Material.YELLOW_BUNDLE,
            Material.LIME_BUNDLE,
            Material.PINK_BUNDLE,
            Material.GRAY_BUNDLE,
            Material.LIGHT_GRAY_BUNDLE,
            Material.CYAN_BUNDLE,
            Material.PURPLE_BUNDLE,
            Material.BLUE_BUNDLE,
            Material.BROWN_BUNDLE,
            Material.GREEN_BUNDLE,
            Material.RED_BUNDLE,
            Material.BLACK_BUNDLE
    ));

    private boolean isBundle(Material material) {
        return ALL_BUNDLES.contains(material);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        // Quick null check for performance
        if (item == null || !isBundle(item.getType())) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();

        // Cancel any interaction with bundles
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK ||
                action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
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
        boolean currentIsBundle = currentItem != null && isBundle(currentItem.getType());
        boolean cursorIsBundle = cursorItem != null && isBundle(cursorItem.getType());

        if (!currentIsBundle && !cursorIsBundle) {
            return; // No bundles involved, skip
        }

        // Cancel ALL interactions with bundles - this covers:
        // - Right-clicking bundles (opening them)
        // - Left-clicking bundles
        // - Shift-clicking bundles
        // - Putting items into bundles
        // - Taking items out of bundles
        // - Moving bundles around inventory
        // - Any other click type on bundles
        event.setCancelled(true);
        player.sendMessage("§cBundles are disabled on this server!");
    }



    // Prevent swapping bundles to offhand
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHand = event.getMainHandItem();
        ItemStack offHand = event.getOffHandItem();

        if ((mainHand != null && isBundle(mainHand.getType())) ||
                (offHand != null && isBundle(offHand.getType()))) {
            event.setCancelled(true);
            player.sendMessage("§cBundles are disabled on this server!");
        }
    }
}