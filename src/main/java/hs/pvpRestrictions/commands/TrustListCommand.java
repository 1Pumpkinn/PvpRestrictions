package hs.pvpRestrictions.commands;

import hs.pvpRestrictions.TrustManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrustListCommand implements CommandExecutor {

    private final TrustManager trustManager;

    public TrustListCommand(TrustManager trustManager) {
        this.trustManager = trustManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        List<String> trusted = trustManager.getTrustedPlayers(player);

        if (trusted.isEmpty()) {
            player.sendMessage(Component.text("You don't have any trusted allies.").color(NamedTextColor.YELLOW));
            player.sendMessage(Component.text("Use /trust <player> to send trust requests!").color(NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("Your trusted allies:").color(NamedTextColor.GREEN));
            for (String trustedPlayer : trusted) {
                player.sendMessage(Component.text("• " + trustedPlayer).color(NamedTextColor.WHITE));
            }
            player.sendMessage(Component.text("Use /untrust <player> to remove an ally.").color(NamedTextColor.GRAY));
        }

        return true;
    }
}