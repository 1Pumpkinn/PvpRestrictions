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
                break;

            case "disable":
            case "off":
            case "false":
                endPortalManager.setEndPortalsEnabled(false);
                Bukkit.broadcastMessage("§c§lEnd portals have been DISABLED!");
                break;

            case "toggle":
                boolean newState = !endPortalManager.areEndPortalsEnabled();
                endPortalManager.setEndPortalsEnabled(newState);
                Bukkit.broadcastMessage("§eEnd portals have been " +
                        (newState ? "§a§lENABLED" : "§c§lDISABLED") + "!");
                break;

            case "status":
                boolean enabled = endPortalManager.areEndPortalsEnabled();
                sender.sendMessage("§eEnd portals are currently: " + (enabled ? "§aENABLED" : "§cDISABLED"));
                break;

            default:
                sender.sendMessage("§cUsage: /endportals <enable|disable|toggle|status>");
                break;
        }

        return true;
    }
}
