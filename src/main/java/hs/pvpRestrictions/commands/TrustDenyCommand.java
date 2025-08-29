package hs.pvpRestrictions.commands;

import hs.pvpRestrictions.TrustManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrustDenyCommand implements CommandExecutor {

    private final TrustManager trustManager;

    public TrustDenyCommand(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        trustManager.denyTrustRequest(player);
        return true;
    }
}