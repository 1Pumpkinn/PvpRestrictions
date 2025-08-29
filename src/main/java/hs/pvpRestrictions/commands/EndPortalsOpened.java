package hs.pvpRestrictions.commands;

import hs.pvpRestrictions.DisableEndPortals;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EndPortalsOpened implements CommandExecutor {

    private final DisableEndPortals endPortalManager;

    public EndPortalsOpened(DisableEndPortals endPortalManager) {
        this.endPortalManager = endPortalManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pvprestrictions.endportals")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            // Show current status
            boolean enabled = endPortalManager.areEndPortalsEnabled();
            sender.sendMessage("§eEnd portals are currently: " + (enabled ? "§aENABLED" : "§cDISABLED"));
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "enable":
            case "on":
            case "true":
                endPortalManager.setEndPortalsEnabled(true);
                Bukkit.broadcastMessage("§a§lEnd portals have been ENABLED!");
                sender.sendMessage("§aEnd portals are now enabled.");
                break;

            case "disable":
            case "off":
            case "false":
                endPortalManager.setEndPortalsEnabled(false);
                Bukkit.broadcastMessage("§c§lEnd portals have been DISABLED!");
                sender.sendMessage("§cEnd portals are now disabled.");
                break;

            case "toggle":
                boolean newState = !endPortalManager.areEndPortalsEnabled();
                endPortalManager.setEndPortalsEnabled(newState);
                String stateMessage = newState ? "§a§lENABLED" : "§c§lDISABLED";
                Bukkit.broadcastMessage("§eEnd portals have been " + stateMessage + "!");
                sender.sendMessage("§eEnd portals are now " + (newState ? "§aenabled" : "§cdisabled") + ".");
                break;

            case "status":
                boolean enabled = endPortalManager.areEndPortalsEnabled();
                sender.sendMessage("§eEnd portals are currently: " + (enabled ? "§aENABLED" : "§cDISABLED"));
                break;

            default:
                sender.sendMessage("§cUsage: /endportals <enable|disable|toggle|status>");
                sender.sendMessage("§cOr just /endportals to check current status");
                break;
        }

        return true;
    }
}